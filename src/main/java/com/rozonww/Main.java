package com.rozonww;

import com.rozonww.models.EchoRequest;
import com.rozonww.models.EchoResponse;
import com.rozonww.EmailService;

import io.javalin.Javalin;
import jakarta.mail.*;

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
    }
}

