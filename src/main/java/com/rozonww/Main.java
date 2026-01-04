package com.rozonww;

import com.rozonww.echo.models.EchoRequest;
import com.rozonww.echo.models.EchoResponse;
import com.rozonww.middleware.RateLimiter;
import com.rozonww.models.Submission;
import com.rozonww.repositories.SubmissionRepository;
import com.rozonww.services.mailer.models.MailerConfiguration;
import com.rozonww.services.mailer.models.MailerSendRequest;
import com.rozonww.services.mailer.MailerService;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;

import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import java.util.Collection;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Dotenv env = getEnvironmentValues();
        MailerService mailer = getMailerService(env);
        DataSource pgDataSource = getDataSource(env);

        var app = Javalin.create(config -> {
                    config.bundledPlugins.enableCors(corsConfig -> {
                        corsConfig.addRule(CorsPluginConfig.CorsRule::anyHost);
                    });
                })
                .start(Integer.parseInt(env.get("SERVER_PORT")));

        app.get("/", ctx -> {
            ctx.result("Caminante, no hay camino.");
        });

        app.post("/echo", ctx -> {
            var req = ctx.bodyAsClass(EchoRequest.class);
            var res = new EchoResponse();

            res.message = req.message;
            ctx.json(res);
        });

        app.before("/call-for-speakers", RateLimiter::checkRequest);

        app.post("/call-for-speakers", ctx -> {
            MailerSendRequest req = ctx.bodyAsClass(MailerSendRequest.class);
            req.validate();

            Submission submission = new Submission(
                    req.name,
                    req.email,
                    req.title,
                    req.description,
                    req.submittedAt
            );

            SubmissionRepository submissionRepo = new SubmissionRepository(pgDataSource);
            Submission result = submissionRepo.insert(submission);

            mailer.send(result.email, result.title, result.description);
            ctx.json(Map.of("success", true));
        });

        // Exception Handling
        // I don't know that we need this due to our manual validation
        app.exception(ValidationException.class, (e, ctx) -> {
            var messages = e.getErrors()
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(ValidationError::getMessage)
                    .toList();
            var errorMessage = messages.getFirst();

            ctx.status(400);
            ctx.json(Map.of("success", false, "message", errorMessage));
        });

        app.exception(MailerSendRequest.InvalidRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.json(Map.of("success", false, "message", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500).json(Map.of("success", false, "message", "Internal server error"));
        });
    }

    public static Dotenv getEnvironmentValues() {
        return Dotenv
                .configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
    }

    public static MailerService getMailerService(Dotenv env) {
        MailerConfiguration config = new MailerConfiguration(
                env.get("SMTP_HOST"),
                Integer.parseInt(env.get("SMTP_PORT")),
                env.get("SMTP_USERNAME"),
                env.get("SMTP_API_KEY"),
                env.get("SMTP_SENDER")
        );

        return new MailerService(config);
    }

    public static DataSource getDataSource(Dotenv env) {
        String host = env.get("DATABASE_HOST");
        String port = env.get("DATABASE_PORT");
        String dbName = env.get("DATABASE_NAME");
        String dbUser = env.get("DATABASE_USER");
        String dbPassword = env.get("DATABASE_PASSWORD");

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        System.out.println("Using JDBC URL: " + jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setMaximumPoolSize(5);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

        // TODO: Proper error handling here
        // Create pool and init db
        DataSource dataSource = new HikariDataSource(config);
        return initDb(dataSource);
    }

    public static DataSource initDb(DataSource dataSource) {
        try {
            Connection dbConnection = dataSource.getConnection();
            Statement sqlStatement = dbConnection.createStatement();

            String createTableSql = """
                    create extension if not exists pgcrypto;
                    create table if not exists submissions (
                        id uuid primary key default gen_random_uuid(),
                        name text not null,
                        email text not null,
                        title text not null,
                        description text not null,
                        submitted_at timestamptz not null,
                        created_at timestamptz not null default now()
                    );
                    """;

            sqlStatement.execute(createTableSql);
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database:", e);
        }
    }
}
