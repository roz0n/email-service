package com.rozonww;

import com.rozonww.models.EchoRequest;
import com.rozonww.models.EchoResponse;
import com.rozonww.services.mailer.MailerService;
import com.rozonww.services.mailer.MailerConfiguration;
import com.rozonww.services.mailer.MailerSendRequest;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
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
            // Get request body
            var req = ctx.bodyAsClass(MailerSendRequest.class);

            // TODO: Validate request body
            // If valid, send the email using email service
            // If not, return a JSON error

            // Create mailer and send email
            Dotenv env = Dotenv.load();
            MailerConfiguration config = new MailerConfiguration(
                    env.get("STMP_HOST"),
                    Integer.parseInt(env.get("STMP_PORT")),
                    env.get("STMP_USERNAME"),
                    env.get("STMP_API_KEY"),
                    env.get("STMP_SENDER")
            );
            MailerService mailer = new MailerService(config);

            mailer.send(req.recipientEmail,
                    req.subject,
                    req.body);
        });
    }
}

