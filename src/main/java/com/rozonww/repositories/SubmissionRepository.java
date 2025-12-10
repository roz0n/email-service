package com.rozonww.repositories;

import com.rozonww.models.Submission;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.UUID;

public class SubmissionRepository {

    private final DataSource dataSource;

    public SubmissionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Submission insert(Submission submission) throws Exception {
        String sql = """
                insert into submissions (name, email, title, description, submitted_at)
                values (?, ?, ?, ?, ?)
                returning id, created_at
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, submission.name);
            statement.setString(2, submission.email);
            statement.setString(3, submission.title);
            statement.setString(4, submission.description);
            statement.setObject(5, submission.submittedAt);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    submission.id = (results.getObject("id", UUID.class));
                    submission.createdAt = results.getObject("created_at", OffsetDateTime.class);
                }
            }
        }

        return submission;
    }
}
