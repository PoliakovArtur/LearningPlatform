package servletTests;

import db.Data;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import model.CourseType;
import model.Teacher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.impl.CourseService;
import service.impl.TeacherService;
import servlets.CoursesServlet;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestCourseServlet {
    private final static List<Course> courses = Data.getCourses();
    private static CourseService courseService;
    private static TeacherService teacherService;
    private static CoursesServlet coursesServlet;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static String RESPONSE_PATH = "src/test/java/servletTests/response.txt";

    @BeforeAll
    public static void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        courseService = mock(CourseService.class);
        coursesServlet = new CoursesServlet(courseService);
    }

    @Test
    public void testGetAll() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
        when(request.getPathInfo()).thenReturn("/");
        when(courseService.findAll()).thenReturn(courses);
        coursesServlet.doGet(request, response);
        String expected = getJsonCourses();
        String actual = readResponse();
        assertEquals(expected, actual);
    }

    @Test
    public void testGet() throws IOException {
        for(Course course : courses) {
            when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
            Long id = course.getId();
            when(courseService.findById(id)).thenReturn(courses.get(id.intValue() - 1));
            when(request.getPathInfo()).thenReturn("/" + id);
            coursesServlet.doGet(request, response);
            String expected = getOutgoingDTO(course).toJSONString();
            String actual = readResponse();
            assertEquals(expected, actual);
        }
    }
    @Test
    public void testPost() throws IOException, ServletException {
        Teacher teacher = new Teacher();
        teacher.setId(2L);
        Course course = new Course(12L, "Совсем новый курс", CourseType.MANAGEMENT, "Представляем вам совсем новый курс", teacher, 5_000_000L);
        String jsonObject = getIncomingDTO(course).toJSONString();
        when(request.getPathInfo()).thenReturn("/");
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.lines()).thenReturn(Stream.of(jsonObject));
        when(request.getReader()).thenReturn(reader);
        coursesServlet.doPost(request, response);
        verify(courseService).save(course);
    }

    @Test
    public void testPut() throws IOException, ServletException {
        Teacher teacher = new Teacher();
        teacher.setId(2L);
        Course course = new Course(12L, "Совсем новый курс", CourseType.MANAGEMENT, "Представляем вам совсем новый курс", teacher, 5_000_000L);
        when(request.getPathInfo()).thenReturn("/" + course.getId());
        BufferedReader reader = mock(BufferedReader.class);
        String jsonObject = getIncomingDTO(course).toJSONString();
        when(reader.lines()).thenReturn(Stream.of(jsonObject));
        when(request.getReader()).thenReturn(reader);
        coursesServlet.doPut(request, response);
        verify(courseService).update(course);
    }

    @Test
    public void testDelete() throws ServletException, IOException {
        for(Course course : courses) {
            Long id = course.getId();
            when(request.getPathInfo()).thenReturn("/" + id);
            coursesServlet.doDelete(request, response);
            verify(courseService).deleteById(id);
        }
    }

    private String getJsonCourses() {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonCourses = new JSONArray();
        for(Course Course : courses) {
            jsonCourses.add(getOutgoingDTO(Course));
        }
        outGoingDTO.put("courses", jsonCourses);
        return outGoingDTO.toJSONString();
    }

    private JSONObject getOutgoingDTO(Course course) {
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
        return jsonCourse;
    }

    private JSONObject getIncomingDTO(Course course) {
        JSONObject jsonCourse = new JSONObject();
        jsonCourse.put("id", course.getId());
        jsonCourse.put("name", course.getName());
        jsonCourse.put("description", course.getDescription());
        jsonCourse.put("type", course.getType().toString());
        jsonCourse.put("price", course.getPrice());
        jsonCourse.put("teacher_id", course.getTeacher().getId());
        return jsonCourse;
    }


    private String readResponse() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(RESPONSE_PATH));
        return reader.lines().reduce(String::concat).orElse("");
    }
}
