package com.rozonww.middleware;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.javalin.http.Context;
import io.javalin.http.TooManyRequestsResponse;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RateLimiter {
    // Each client will create a new entry
    private static final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 5 reqs. per min
    private static final Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));

    private RateLimiter() {
    }

    public static void checkRequest(Context ctx) throws TooManyRequestsResponse {
        String client = getClientAddress(ctx);

        Bucket bucket = buckets.computeIfAbsent(client, key -> Bucket.builder().addLimit(limit).build());
        boolean isAllowed = bucket.tryConsume(1);

        if (!isAllowed) {
            throw new TooManyRequestsResponse("Rate limit exceeded");
        }
    }

    public static String getClientAddress(Context ctx) {
        String forwarded = ctx.header("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return ctx.req().getRemoteAddr();
    }
}
