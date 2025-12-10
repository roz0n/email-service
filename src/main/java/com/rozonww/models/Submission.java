package com.rozonww.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Submission {
    public UUID id; // db fills in for us
    public String name;
    public String email;
    public String title;
    public String description;
    public OffsetDateTime createdAt; // db fills this with now()
    public OffsetDateTime submittedAt;


    public Submission(String name,
                      String email,
                      String title,
                      String description,
                      OffsetDateTime submittedAt) {
        this.name = name;
        this.email = email;
        this.title = title;
        this.description = description;
        this.submittedAt = submittedAt;
    }
}
