package com.rozonww;

import com.rozonww.models.EchoRequest;
import com.rozonww.models.EchoResponse;
import com.rozonww.services.mailer.MailerConfiguration;
import com.rozonww.services.mailer.MailerSendRequest;
import com.rozonww.services.mailer.MailerService;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.validation.ValidationException;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Dotenv env = Dotenv
                .configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
        MailerConfiguration config = new MailerConfiguration(
                env.get("SMTP_HOST"),
                Integer.parseInt(env.get("SMTP_PORT")),
                env.get("SMTP_USERNAME"),
                env.get("SMTP_API_KEY"),
                env.get("SMTP_SENDER")
        );
        MailerService mailer = new MailerService(config);

        var app = Javalin.create()
                .get("/", ctx -> ctx.result("Hola mundo"))
                .start(8080);

        app.post("/echo", ctx -> {
            var req = ctx.bodyAsClass(EchoRequest.class);
            var res = new EchoResponse();

            res.message = req.message;
            ctx.json(res);
        });

        app.post("/send", ctx -> {
            MailerSendRequest req = ctx.bodyValidator(MailerSendRequest.class)
                    .check(body -> body.recipientEmail != null && !body.recipientEmail.isBlank(), "Recipient email is required")
                    .check(body -> body.subject != null && !body.subject.isBlank(), "Subject is required")
                    .check(body -> body.body != null && !body.body.isBlank(), "Body is required")
                    .get();

            mailer.send(req.recipientEmail, req.subject, req.body);
            ctx.json(Map.of("success", true));
        });

        app.exception(ValidationException.class, (e, ctx) -> {
            var messages = e.getErrors()
                    .values()
                    .stream()
                    .flatMap(list -> list.stream())
                    .map(err -> err.getMessage())
                    .toList();
            var errorMessage = messages.getFirst();

            ctx.status(400);
            ctx.json(Map.of("success", false, "message", errorMessage));
        });
    }
}

