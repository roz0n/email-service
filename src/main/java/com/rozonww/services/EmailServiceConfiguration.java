package com.rozonww.services;

public class EmailServiceConfiguration {
    public EmailServiceConfiguration(String smtpHost,
                                     int smtpPort,
                                     String username,
                                     String password,
                                     String from) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.from = from;
    }

    String smtpHost;
    int smtpPort;
    String username;
    String password;
    String from;
}
