package repository.impl;

import db.ConnectionManager;
import model.Teacher;
import repository.mapper.TeacherRSMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherRepository implements Repository<Teacher, Long> {
    private final TeacherRSMapper mapper = new TeacherRSMapper();
    private final SQLQueryBuilder builder = new SQLQueryBuilder();

    @Override
    public Optional<Teacher> findById(Long id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("teachers")
                    .join("LEFT", "courses", "teacher_id", "teachers", "id")
                    .where("teachers.id", id.toString());
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return Optional.ofNullable(mapper.map(resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            builder
                    .delete("teachers")
                    .where("id", id.toString());
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            int deleteRows = statement.executeUpdate(query);
            return deleteRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Teacher> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("teachers")
                    .join("LEFT", "courses", "teacher_id", "teachers", "id");
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return mapper.mapAll(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Teacher save(Teacher teacher) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String name = teacher.getName();
            String salary = teacher.getSalary().toString();
            String age = teacher.getAge().toString();
            builder
                    .insertInto("teachers", "name", name, "salary", salary, "age", age);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Teacher update(Teacher teacher) {
        try (Connection connection = ConnectionManager.getConnection()) {
            List<String> updatedColumns = new ArrayList<>();
            if (teacher.getName() != null) {
                updatedColumns.add("name");
                updatedColumns.add(teacher.getName());
            }
            if (teacher.getSalary() != null) {
                updatedColumns.add("salary");
                updatedColumns.add(teacher.getSalary().toString());
            }
            if (teacher.getAge() != null) {
                updatedColumns.add("age");
                updatedColumns.add(teacher.getAge().toString());
            }
            String[] cAndVPairs = new String[updatedColumns.size()];
            String id = teacher.getId().toString();
            builder
                    .update("teachers", updatedColumns.toArray(cAndVPairs))
                    .where("id", id);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
