package repository.impl;

import db.ConnectionManager;
import model.Course;
import repository.mapper.CourseRSMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepository implements Repository<Course, Long> {

    private final SQLQueryBuilder builder;
    private final CourseRSMapper mapper;

    public CourseRepository() {
        builder = new SQLQueryBuilder();
        mapper = new CourseRSMapper();
    }

    @Override
    public Optional<Course> findById(Long id) {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("teachers")
                    .join("RIGHT", "courses", "teacher_id", "teachers", "id")
                    .join("LEFT", "subscriptions", "course_id", "courses", "id")
                    .join("LEFT","students", "id", "subscriptions", "student_id")
                    .where("courses.id", id.toString());
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            return Optional.of(mapper.map(set));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .delete("courses")
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
    public List<Course> findAll() {
        try(Connection connection = ConnectionManager.getConnection()) {
            builder
                    .selectAll("teachers")
                    .join("RIGHT","courses", "teacher_id", "teachers", "id")
                    .join("LEFT","subscriptions", "course_id", "courses", "id")
                    .join("LEFT","students", "id", "subscriptions", "student_id");
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);
            return mapper.mapAll(set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course save(Course course) {
        try(Connection connection = ConnectionManager.getConnection()) {
            String name = course.getName();
            String type = course.getType().toString();
            String description = course.getDescription();
            String price = course.getPrice().toString();
            String teacherId = course.getTeacher().getId().toString();
            builder
                    .insertInto("courses",
                            "name", name,
                            "type", type,
                            "description", description,
                            "price", price,
                            "teacher_id", teacherId);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course update(Course course) {
        try(Connection connection = ConnectionManager.getConnection()) {
            List<String> updatedColumns = new ArrayList<>();
            if(course.getName() != null) {
                updatedColumns.add("name");
                updatedColumns.add(course.getName());
            }
            if(course.getType() != null) {
                updatedColumns.add("type");
                updatedColumns.add(course.getType().toString());
            }
            if(course.getDescription() != null) {
                updatedColumns.add("description");
                updatedColumns.add(course.getDescription());
            }
            if(course.getPrice() != null) {
                updatedColumns.add("price");
                updatedColumns.add(course.getPrice().toString());
            }
            if(course.getTeacher() != null) {
                updatedColumns.add("teacher_id");
                updatedColumns.add(course.getTeacher().getId().toString());
            }
            String[] cAndVPairs = new String[updatedColumns.size()];
            String id = course.getId().toString();
            builder
                    .update("courses", updatedColumns.toArray(cAndVPairs))
                    .where("id", id);
            String query = builder.getQuery();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
