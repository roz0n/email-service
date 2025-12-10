package com.rozonww.services.mailer.models;

public class MailerConfiguration {
    public MailerConfiguration(String smtpHost,
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

    public String smtpHost;
    public int smtpPort;
    public String username;
    public String password;
    public String from;
}
