package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Student;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.Service;
import service.impl.StudentService;
import servlets.mapper.StudentDTOMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class StudentsServlet extends HttpServlet {

    private StudentService service;
    private JSONParser jsonParser = new JSONParser();

    public StudentsServlet(StudentService service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String info = req.getPathInfo();
        String response = "";
        try {
            if(info == null || info.equals("/")) {
                List<Student> students = service.findAll();
                JSONObject outGoingDTO = StudentDTOMapper.mapAll(students);
                response = outGoingDTO.toJSONString();
            } else {
                Long id = Long.parseLong(info.substring(1));
                Student student = service.findById(id);
                JSONObject object = StudentDTOMapper.map(student);
                response = object.toJSONString();
            }
        } catch (NumberFormatException | NullPointerException exception) {
            exception.printStackTrace();
            resp.sendError(404);
        }
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        Writer writer = resp.getWriter();
        writer.write(response);
        writer.flush();
        writer.close();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if(info == null || info.equals("/")) {
            try {
                BufferedReader reader = req.getReader();
                String jsonString = reader.lines().reduce(String::concat).get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
                Student student = StudentDTOMapper.map(jsonObject);
                service.save(student);
            } catch (NullPointerException | ParseException exception) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if(info != null && info.matches("/\\d+")) {
            try {
                BufferedReader reader = req.getReader();
                String jsonString = reader.lines().reduce(String::concat).get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
                Student student = StudentDTOMapper.map(jsonObject);
                service.update(student);
            } catch (NullPointerException | ParseException ex) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if(info != null && info.matches("/\\d+")) {
            try {
                Long id = Long.parseLong(info.substring(1));
                service.deleteById(id);
            } catch (NullPointerException | NumberFormatException ex) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }
}
