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
import service.impl.TeacherService;
import servlets.TeachersServlet;
import java.io.*;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestTeacherServlet {
    private final static List<Teacher> teachers = Entities.getTeachers();
    private static TeacherService TeacherService;
    private static TeachersServlet teachersServlet;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static String RESPONSE_PATH = "src/test/java/servletTests/response.txt";

    @BeforeAll
    public static void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        TeacherService = mock(TeacherService.class);
        teachersServlet = new TeachersServlet(TeacherService);
    }

    @Test
    public void testGetAll() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
        when(request.getPathInfo()).thenReturn("/");
        when(TeacherService.findAll()).thenReturn(teachers);
        teachersServlet.doGet(request, response);
        String expected = getJsonTeachers();
        String actual = readResponse();
        assertEquals(expected, actual);
    }

    @Test
    public void testGet() throws IOException {
        for (Teacher Teacher : teachers) {
            when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
            Long id = Teacher.getId();
            when(TeacherService.findById(id)).thenReturn(teachers.get(id.intValue() - 1));
            when(request.getPathInfo()).thenReturn("/" + id);
            teachersServlet.doGet(request, response);
            String expected = getJsonObject(Teacher).toJSONString();
            String actual = readResponse();
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testPost() throws IOException, ServletException {
        Teacher Teacher = new Teacher(30L, "Картошин Виктор", 40000L, 33);
        String jsonObject = getJsonObject(Teacher).toString();
        when(request.getPathInfo()).thenReturn("/");
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.lines()).thenReturn(Stream.of(jsonObject));
        when(request.getReader()).thenReturn(reader);
        teachersServlet.doPost(request, response);
        verify(TeacherService).save(Teacher);
    }

    @Test
    public void testPut() throws IOException, ServletException {
        for (Teacher Teacher : teachers) {
            Long id = Teacher.getId();
            when(request.getPathInfo()).thenReturn("/" + id);
            BufferedReader reader = mock(BufferedReader.class);
            String jsonObject = getJsonObject(Teacher).toJSONString();
            when(reader.lines()).thenReturn(Stream.of(jsonObject));
            when(request.getReader()).thenReturn(reader);
            teachersServlet.doPut(request, response);
            verify(TeacherService).update(Teacher);
        }
    }

    @Test
    public void testDelete() throws ServletException, IOException {
        for (Teacher Teacher : teachers) {
            Long id = Teacher.getId();
            when(request.getPathInfo()).thenReturn("/" + id);
            teachersServlet.doDelete(request, response);
            verify(TeacherService).deleteById(id);
        }
    }

    private String getJsonTeachers() {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonTeachers = new JSONArray();
        for (Teacher Teacher : teachers) {
            jsonTeachers.add(getJsonObject(Teacher));
        }
        outGoingDTO.put("teachers", jsonTeachers);
        return outGoingDTO.toJSONString();
    }

    private JSONObject getJsonObject(Teacher Teacher) {
        JSONObject jsonTeacher = new JSONObject();
        jsonTeacher.put("id", Teacher.getId());
        jsonTeacher.put("name", Teacher.getName());
        jsonTeacher.put("salary", Teacher.getSalary());
        jsonTeacher.put("age", Teacher.getAge());
        return jsonTeacher;
    }


    private String readResponse() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(RESPONSE_PATH));
        return reader.lines().reduce(String::concat).orElse("");
    }
}
