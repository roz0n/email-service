package com.rozonww;

import com.rozonww.models.EchoRequest;
import com.rozonww.models.EchoResponse;
import com.rozonww.services.EmailService;
import com.rozonww.services.EmailServiceConfiguration;

import io.javalin.Javalin;
import io.github.cdimascio.dotenv.Dotenv;

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

            // Create mailer and send email
            Dotenv env = Dotenv.load();
            EmailServiceConfiguration config = new EmailServiceConfiguration(
                    env.get("STMP_HOST"),
                    Integer.parseInt(env.get("STMP_PORT")),
                    env.get("STMP_USERNAME"),
                    env.get("STMP_API_KEY"),
                    env.get("STMP_SENDER")
            );
            EmailService mailer = new EmailService(config);

            mailer.send("",
                    "Your paper was received!",
                    "Your submission was received. We'll take a look and get back to you shortly.");
        });
    }
}
