package com.rozonww.services.mailer;

public class MailerSendRequest {
    public String recipientEmail;
    public String subject;
    public String body;

    public static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    public static class InvalidRequestException extends RuntimeException {
        public  InvalidRequestException(String message) {
            super(message);
        }
    }

    public void validate() {
        if (!validateEmail(recipientEmail)) {
            throw new InvalidRequestException("Email is invalid");
        }

        if (!validateSubject(subject)) {
            throw new InvalidRequestException("Subject is invalid");
        }

        if (!validateBody(body)) {
            throw new InvalidRequestException("Body is invalid");
        }
    }

    private boolean validateEmail(String email) {
        return email != null && !email.isBlank() && email.matches(EMAIL_REGEX);
    }

    private boolean validateSubject(String subject) {
        return subject != null && !subject.isBlank() && subject.length() >= 10;
    }

    private boolean validateBody(String body) {
        return body != null && !body.isBlank() && body.length() >= 180;
    }
}
