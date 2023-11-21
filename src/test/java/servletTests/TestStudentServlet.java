package servletTests;

import db.Data;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.impl.StudentService;
import servlets.StudentsServlet;
import java.io.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestStudentServlet {
    private final static List<Student> students = Data.getStudents();
    private static StudentService studentService;
    private static StudentsServlet studentsServlet;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static String RESPONSE_PATH = "src/test/java/servletTests/response.txt";

    @BeforeAll
    public static void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        studentService = mock(StudentService.class);
        studentsServlet = new StudentsServlet(studentService);
    }

    @Test
    public void testGetAll() throws IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
        when(request.getPathInfo()).thenReturn("/");
        when(studentService.findAll()).thenReturn(students);
        studentsServlet.doGet(request, response);
        String expected = getJsonStudents();
        String actual = readResponse();
        assertEquals(expected, actual);
    }

    @Test
    public void testGet() throws IOException {
        for(Student student : students) {
            when(response.getWriter()).thenReturn(new PrintWriter(RESPONSE_PATH));
            Long id = student.getId();
            when(studentService.findById(id)).thenReturn(students.get(id.intValue() - 1));
            when(request.getPathInfo()).thenReturn("/" + id);
            studentsServlet.doGet(request, response);
            String expected = getJsonObject(student).toJSONString();
            String actual = readResponse();
            assertEquals(expected, actual);
        }
    }
    @Test
    public void testPost() throws IOException, ServletException {
        Student student = new Student(30L, "Картошин Виктор", 40, Date.valueOf("2022-10-13"));
        String jsonObject = getJsonObject(student).toJSONString();
        when(request.getPathInfo()).thenReturn("/");
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.lines()).thenReturn(Stream.of(jsonObject));
        when(request.getReader()).thenReturn(reader);
        studentsServlet.doPost(request, response);
        verify(studentService).save(student);
    }

    @Test
    public void testPut() throws IOException, ServletException {
        for(Student student : students) {
            Long id = student.getId();
            when(request.getPathInfo()).thenReturn("/" + id);
            BufferedReader reader = mock(BufferedReader.class);
            String jsonObject = getJsonObject(student).toJSONString();
            when(reader.lines()).thenReturn(Stream.of(jsonObject));
            when(request.getReader()).thenReturn(reader);
            studentsServlet.doPut(request, response);
            verify(studentService).update(student);
        }
    }

    @Test
    public void testDelete() throws ServletException, IOException {
        for(Student student : students) {
            Long id = student.getId();
            when(request.getPathInfo()).thenReturn("/" + id);
            studentsServlet.doDelete(request, response);
            verify(studentService).deleteById(id);
        }
    }

    private String getJsonStudents() {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonStudents = new JSONArray();
        for(Student student : students) {
            jsonStudents.add(getJsonObject(student));
        }
        outGoingDTO.put("students", jsonStudents);
        return outGoingDTO.toJSONString();
    }

    private JSONObject getJsonObject(Student student) {
        JSONObject jsonStudent = new JSONObject();
        jsonStudent.put("id", student.getId());
        jsonStudent.put("name", student.getName());
        jsonStudent.put("age", student.getAge());
        jsonStudent.put("registration_date", student.getRegistrationDate().toString());
        return jsonStudent;
    }


    private String readResponse() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(RESPONSE_PATH));
        return reader.lines().reduce(String::concat).orElse("");
    }
}
