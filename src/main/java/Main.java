import db.DBLoader;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.json.simple.parser.ParseException;
import service.impl.CourseService;
import service.impl.StudentService;
import service.impl.SubscriptionService;
import service.impl.TeacherService;
import servlets.*;
import java.io.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) throws LifecycleException, SQLException, IOException, ParseException {
        DBLoader.clearDB();
        DBLoader.loadEntities();
        Tomcat tomcat = new Tomcat();
        tomcat.getConnector().setPort(8081);
        Context context = tomcat.addContext("", null);;
        initServlets(context);
        tomcat.start();
        System.out.println("http://localhost:8081/");
    }


    private static void initServlets(Context context) {
        TeacherService teacherService = ServiceFactory.teacherService();
        StudentService studentService = ServiceFactory.studentService();
        CourseService courseService = ServiceFactory.courseService(teacherService);
        SubscriptionService subscriptionService = ServiceFactory.subscriptionService(studentService, courseService);

        MainPageServlet mainPageServlet = new MainPageServlet();
        CoursesServlet coursesServlet = new CoursesServlet(courseService);
        StudentsServlet studentsServlet = new StudentsServlet(studentService);
        TeachersServlet teachersServlet = new TeachersServlet(teacherService);
        SubscriptionsServlet subscriptionsServlet = new SubscriptionsServlet(subscriptionService);

        Tomcat.addServlet(context, "MainServlet", mainPageServlet)
                .addMapping("/");
        Tomcat.addServlet(context, "CoursesServlet", coursesServlet)
                .addMapping("/courses/*");
        Tomcat.addServlet(context, "StudentsServlet", studentsServlet)
                .addMapping("/students/*");
        Tomcat.addServlet(context, "TeachersServlet", teachersServlet)
                .addMapping("/teachers/*");
        Tomcat.addServlet(context, "SubscriptionServlet", subscriptionsServlet).addMapping("/subscriptions/*");
    }
}

