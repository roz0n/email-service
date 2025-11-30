package com.rozonww;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        var app = Javalin.create()
                .get("/", ctx -> ctx.result("Hola mundo"))
                .start(8080);

        app.get("/echo", (ctx) -> {
            ctx.result(ctx.toString());
        });

        app.post("/echo", ctx -> {
           var req = ctx.body();
           ctx.result("Echo:" + "\n" + req);
        });
    }
}