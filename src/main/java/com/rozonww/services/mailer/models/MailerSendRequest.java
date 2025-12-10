package com.rozonww.services.mailer.models;

import java.time.OffsetDateTime;

public class MailerSendRequest {
    public String name;
    public String email;
    public String title;
    public String description;
    public OffsetDateTime submittedAt;

    public static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }

    public void validate() {
        if (!validateName(name)) {
            throw new InvalidRequestException("Name is invalid");
        }

        if (!validateEmail(email)) {
            throw new InvalidRequestException("Email is invalid");
        }

        if (!validateTitle(title)) {
            throw new InvalidRequestException("Title is invalid");
        }

        if (!validateDescription(description)) {
            throw new InvalidRequestException("Body is invalid");
        }

        if (!validateSubmitDate(submittedAt)) {
            throw new InvalidRequestException("Submitted date is invalid");
        }
    }

    private boolean validateName(String name) {
        return name != null && !name.isBlank();
    }

    private boolean validateEmail(String email) {
        return email != null && !email.isBlank() && email.matches(EMAIL_REGEX);
    }

    private boolean validateTitle(String title) {
        return title != null && !title.isBlank() && title.length() >= 10;
    }

    private boolean validateDescription(String description) {
        return description != null && !description.isBlank() && description.length() >= 180;
    }

    private boolean validateSubmitDate(OffsetDateTime submittedAt) {
        return submittedAt != null && !submittedAt.toString().isBlank();
    }

}
