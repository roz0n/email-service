package com.rozonww.services.mailer;

import io.javalin.validation.ValidationError;

public class MailerSendRequest {
    public String recipientEmail;
    public String subject;
    public String body;

    public class InvalidRequestException extends RuntimeException {
        public  InvalidRequestException(String message) {
            super(message);
        }
    }

    public boolean validate() {
        if (!validateEmail(recipientEmail)) {
            throw new InvalidRequestException("Email is invalid");
        }

        if (!validateEmail(subject)) {
            throw new InvalidRequestException("Subject is invalid");
        }

        if (!validateEmail(body)) {
            throw new InvalidRequestException("Body is invalid");
        }

        return true;
    }

    private boolean validateEmail(String email) {
        return email != null && !email.isBlank();
    }

    private boolean validateSubject(String subject) {
        return subject != null && !subject.isBlank();
    }

    private boolean validateBody(String body) {
        return body != null && !body.isBlank();
    }
}
