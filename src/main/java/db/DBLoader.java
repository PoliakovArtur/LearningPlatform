package db;

import repository.impl.CourseRepository;
import repository.impl.StudentRepository;
import repository.impl.SubscriptionRepository;
import repository.impl.TeacherRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static db.Entities.*;

public class DBLoader {
    private final static String SCRIPT_PATH = "src/main/resources/script.sql";

    private DBLoader() {}

    public static void clearDB() throws SQLException {
        ConnectionManager.readProperties();
        String database = quitDB();
        Connection connection = ConnectionManager.getConnection();
        try(Statement statement = connection.createStatement()) {
            statement.execute("drop database if exists " + database);
            statement.execute("create database " + database);
            statement.execute("use " + database);
            BufferedReader reader = new BufferedReader(new FileReader(SCRIPT_PATH));
            reader.lines().forEach(q -> {
                try {
                    statement.execute(q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        connection.close();
        ConnectionManager.readProperties();
    }

    private static String quitDB() {
        String afterLocalHost = "(?<=localhost:\\d{4}/).*";
        Pattern pattern = Pattern.compile(afterLocalHost);
        String url = ConnectionManager.getUrl();
        Matcher matcher = pattern.matcher(url);
        String secondPartOfUrl = "";
        while (matcher.find()) {
            secondPartOfUrl = matcher.group();
        }
        String database = secondPartOfUrl.substring(0, secondPartOfUrl.indexOf('?'));
        secondPartOfUrl = secondPartOfUrl.replaceFirst(".*(?=\\?)", "");
        url = url.replaceAll(afterLocalHost, "");
        if (!secondPartOfUrl.isEmpty()) {
            url = url.concat(secondPartOfUrl);
        }
        ConnectionManager.setUrl(url);
        return database;
    }

    public static void loadEntities() {
        TeacherRepository teacherRepository = new TeacherRepository();
        StudentRepository studentRepository = new StudentRepository();
        CourseRepository courseRepository = new CourseRepository();
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
        getTeachers().forEach(teacherRepository::save);
        getStudents().forEach(studentRepository::save);
        getCourses().forEach(courseRepository::save);
        getSubscriptions().forEach(subscriptionRepository::save);
    }
}
