package com.rozonww;

import com.rozonww.models.EchoRequest;
import com.rozonww.models.EchoResponse;
import com.rozonww.services.EmailService;
import com.rozonww.services.EmailServiceConfiguration;

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
            // Validate it
            // If valid, send the email using email service
            // If not, return a JSON error
            EmailServiceConfiguration config = new EmailServiceConfiguration(
                    "smtp-relay.brevo.com",
                    587,
                    "9cf67f001@smtp-brevo.com",
                    "STMP API KEY",
                    "noreply@devcolectivo.com"
            );
            EmailService mailer = new EmailService(config);

            mailer.send("",
                    "Your paper was received!",
                    "Your submission was received. We'll take a look and get back to you shortly.");
        });
    }
}
