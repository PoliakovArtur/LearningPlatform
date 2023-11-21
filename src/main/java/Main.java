import db.ConnectionManager;
import db.Data;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.json.simple.parser.ParseException;
import repository.impl.*;
import service.impl.CourseService;
import service.impl.StudentService;
import service.impl.SubscriptionService;
import service.impl.TeacherService;
import servlets.*;
import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws LifecycleException, SQLException, IOException, ParseException {
        clearDB();
        Tomcat tomcat = new Tomcat();
        tomcat.getConnector().setPort(8081);
        Context context = tomcat.addContext("", null);
        loadDB();
        initServlets(context);
        tomcat.start();
        System.out.println("http://localhost:8081/");
    }

    private static void loadDB() throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/learning_platform_script.sql"));
        Connection connection = ConnectionManager.getConnection();
        Statement statement = connection.createStatement();
        reader.lines().forEach(q -> {
            try {
                statement.execute(q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        connection.close();
        reader.close();
    }

    private static void clearDB() throws SQLException {
        ConnectionManager.readProperties();
        String database = quitDatabase();
        Connection connection = ConnectionManager.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("drop database if exists " + database);
        statement.execute("create database " + database);
        connection.close();
        ConnectionManager.readProperties();
    }

    private static String quitDatabase() {
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
        if(!secondPartOfUrl.isEmpty()) {
            url = url.concat(secondPartOfUrl);
        }
        ConnectionManager.setUrl(url);
        return database;
    }

    private static void initServlets(Context context) {
        CourseRepository courseRepository = new CourseRepository();
        StudentRepository studentRepository = new StudentRepository();
        TeacherRepository teacherRepository = new TeacherRepository();
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
        loadData(teacherRepository, Data.getTeachers());
        loadData(courseRepository, Data.getCourses());
        loadData(studentRepository, Data.getStudents());
        loadData(subscriptionRepository, Data.getSubscriptions());
        TeacherService teacherService = new TeacherService(teacherRepository);
        StudentService studentService = new StudentService(studentRepository);
        CourseService courseService = new CourseService(courseRepository, teacherService);
        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, studentService, courseService);
        StudentsServlet studentsServlet = new StudentsServlet(studentService);
        CoursesServlet coursesServlet = new CoursesServlet(courseService);
        TeachersServlet teachersServlet = new TeachersServlet(teacherService);
        SubscriptionsServlet subscriptionsServlet = new SubscriptionsServlet(subscriptionService);
        MainPageServlet mainPageServlet = new MainPageServlet();
        Tomcat.addServlet(context, "MainServlet", mainPageServlet).addMapping("/");
        Tomcat.addServlet(context, "CoursesServlet", coursesServlet).addMapping("/courses/*");
        Tomcat.addServlet(context, "StudentsServlet",studentsServlet).addMapping("/students/*");
        Tomcat.addServlet(context, "TeachersServlet", teachersServlet).addMapping("/teachers/*");
        Tomcat.addServlet(context, "SubscriptionServlet", subscriptionsServlet).addMapping("/subscriptions/*");
    }

    private static <T, K> void loadData(Repository<T, K> repository, List<T> entities) {
        entities.forEach(repository::save);
    }
}

