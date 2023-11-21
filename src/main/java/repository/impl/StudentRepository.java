package repository.impl;

import db.ConnectionManager;
import model.Student;
import repository.mapper.StudentRSMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class StudentRepository implements Repository<Student, Long> {

    private SQLQueryBuilder builder;
    private StudentRSMapper mapper;

    public StudentRepository() {
        builder = new SQLQueryBuilder();
        mapper = new StudentRSMapper();
    }
    @Override
    public Optional<Student> findById(Long id) {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("students")
                    .join("LEFT","subscriptions", "student_id", "students", "id")
                    .join("LEFT","courses", "id", "subscriptions", "course_id")
                    .join("LEFT","teachers", "id", "courses", "teacher_id")
                    .where("students.id", id.toString());
            Statement statement = connection.createStatement();
            String query = builder.getQuery();
            ResultSet set = statement.executeQuery(query);
            return Optional.of(mapper.map(set));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .delete("students")
                    .where("id", id.toString());
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            int deleteRows = statement.executeUpdate(query);
            return deleteRows > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Student> findAll() {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("students")
                    .join("LEFT", "subscriptions", "student_id", "students", "id")
                    .join("LEFT", "courses", "id", "subscriptions", "course_id")
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
    public Student save(Student student) {
        try(Connection connection = ConnectionManager.getConnection()) {
            String name = student.getName();
            String age = student.getAge().toString();
            String registrationDate = student.getRegistrationDate().toString();
            builder
                    .insertInto("students", "name", name, "age", age, "registration_date", registrationDate);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return student;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Student update(Student student) {
        try(Connection connection = ConnectionManager.getConnection()) {
            String id = student.getId().toString();
            List<String> updatedColumns = new ArrayList<>();
            if(student.getName() != null) {
                updatedColumns.add("name");
                updatedColumns.add(student.getName());
            }
            if(student.getRegistrationDate() != null) {
                updatedColumns.add("registration_date");
                updatedColumns.add(student.getRegistrationDate().toString());
            }
            if(student.getAge() != null) {
                updatedColumns.add("age");
                updatedColumns.add(student.getAge().toString());
            }
            String[] cAndVPairs = new String[updatedColumns.size()];
            builder
                    .update("students", updatedColumns.toArray(cAndVPairs))
                    .where("id", id);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return student;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
