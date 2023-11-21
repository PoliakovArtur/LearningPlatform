package repository.impl;

import db.ConnectionManager;
import model.Subscription;
import repository.mapper.SubscriptionRSMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubscriptionRepository implements Repository<Subscription, Map.Entry<Long, Long>> {
    private SQLQueryBuilder builder = new SQLQueryBuilder();
    private SubscriptionRSMapper mapper = new SubscriptionRSMapper();

    @Override
    public Optional<Subscription> findById(Map.Entry<Long, Long> id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String studentId = id.getKey().toString();
            String courseId = id.getValue().toString();
            builder
                    .selectAll("students")
                    .join("INNER", "subscriptions", "student_id", "students", "id")
                    .join("INNER", "courses", "id", "subscriptions", "course_id")
                    .join("LEFT", "teachers", "id", "courses", "teacher_id")
                    .where("subscriptions.student_id", studentId)
                    .and("subscriptions.course_id", courseId);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            return Optional.of(mapper.map(set));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteById(Map.Entry<Long, Long> id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String studentId = id.getKey().toString();
            String courseId = id.getValue().toString();
            builder
                    .delete("subscriptions")
                    .where("student_id", studentId)
                    .and("course_id", courseId);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            int deleteRows = statement.executeUpdate(query);
            return deleteRows > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Subscription> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("students")
                    .join("INNER", "subscriptions", "student_id", "students", "id")
                    .join("INNER", "courses", "id", "subscriptions", "course_id")
                    .join("LEFT", "teachers", "id", "courses", "teacher_id");
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            return mapper.mapAll(set);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Subscription save(Subscription subscription) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String studentId = subscription.getStudent().getId().toString();
            String courseId = subscription.getCourse().getId().toString();
            String subscriptionDate = subscription.getSubscriptionDate().toString();
            builder
                    .insertInto("subscriptions", "student_id", studentId, "course_id", courseId, "subscription_date", subscriptionDate);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return subscription;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Subscription update(Subscription subscription) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String studentId = subscription.getStudent().getId().toString();
            String courseId = subscription.getCourse().getId().toString();
            String subscriptionDate = subscription.getSubscriptionDate().toString();
            builder
                    .update("subscriptions", "subscription_date", subscriptionDate)
                    .where("student_id", studentId)
                    .and("course_id", courseId);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return subscription;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
