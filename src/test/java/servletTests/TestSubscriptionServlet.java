package servletTests;

import db.Entities;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.impl.SubscriptionService;
import servlets.SubscriptionsServlet;
import java.io.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestSubscriptionServlet {
    private final static List<Subscription> subscriptions = Entities.getSubscriptions();
    private static SubscriptionService SubscriptionService;
    private static SubscriptionsServlet SubscriptionsServlet;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static String RESPONSE_PATH = "src/test/java/servletTests/response.txt";

    @BeforeAll
    public static void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        SubscriptionService = mock(SubscriptionService.class);
        SubscriptionsServlet = new SubscriptionsServlet(SubscriptionService);
    }

    @Test
    public void testGetAll() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
        when(request.getPathInfo()).thenReturn("/");
        when(SubscriptionService.findAll()).thenReturn(subscriptions);
        SubscriptionsServlet.doGet(request, response);
        String expected = getJsonSubscriptions();
        String actual = readResponse();
        assertEquals(expected, actual);
    }

    @Test
    public void testPost() throws IOException, ServletException {
        Student student = new Student();
        student.setId(2L);
        Course course = new Course();
        course.setId(4L);
        Subscription subscription = new Subscription(student, course, Date.valueOf("2022-10-10"));
        String jsonObject = getIncomingDTO(subscription).toJSONString();
        when(request.getPathInfo()).thenReturn("/");
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.lines()).thenReturn(Stream.of(jsonObject));
        when(request.getReader()).thenReturn(reader);
        SubscriptionsServlet.doPost(request, response);
        verify(SubscriptionService).save(subscription);
    }

    private String getJsonSubscriptions() {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonSubscriptions = new JSONArray();
        for (Subscription subscription : subscriptions) {
            jsonSubscriptions.add(getOutGoingDTO(subscription));
        }
        outGoingDTO.put("subscriptions", jsonSubscriptions);
        return outGoingDTO.toJSONString();
    }

    private JSONObject getIncomingDTO(Subscription subscription) {
        JSONObject jsonSubscription = new JSONObject();
        jsonSubscription.put("subscription_date", subscription.getSubscriptionDate().toString());
        jsonSubscription.put("student_id", subscription.getStudent().getId());
        jsonSubscription.put("course_id", subscription.getCourse().getId());
        return jsonSubscription;
    }

    private JSONObject getOutGoingDTO(Subscription subscription) {
        JSONObject jsonSubscription = new JSONObject();
        jsonSubscription.put("subscription_date", subscription.getSubscriptionDate().toString());
        JSONObject jsonStudent = new JSONObject();
        Student student = subscription.getStudent();
        jsonStudent.put("id", student.getId());
        jsonStudent.put("name", student.getName());
        jsonStudent.put("age", student.getAge());
        jsonStudent.put("registration_date", student.getRegistrationDate().toString());
        Course course = subscription.getCourse();
        JSONObject jsonCourse = new JSONObject();
        jsonCourse.put("id", course.getId());
        jsonCourse.put("name", course.getName());
        jsonCourse.put("description", course.getDescription());
        jsonCourse.put("type", course.getType().toString());
        jsonCourse.put("price", course.getPrice());
        JSONObject jsonTeacher = new JSONObject();
        Teacher teacher = course.getTeacher();
        jsonTeacher.put("id", teacher.getId());
        jsonTeacher.put("name", teacher.getName());
        jsonTeacher.put("salary", teacher.getSalary());
        jsonTeacher.put("age", teacher.getAge());
        jsonCourse.put("teacher", jsonTeacher);
        jsonSubscription.put("student", jsonStudent);
        jsonSubscription.put("course", jsonCourse);
        return jsonSubscription;
    }


    private String readResponse() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(RESPONSE_PATH));
        return reader.lines().reduce(String::concat).orElse("");
    }
}
