package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.impl.CourseService;
import service.impl.ServiceException;
import servlets.mapper.CourseDTOMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CoursesServlet extends HttpServlet {
    private CourseService service;
    private JSONParser jsonParser = new JSONParser();

    public CoursesServlet(CourseService service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String info = req.getPathInfo();
        String response = "";
        try {
            if (info == null || info.equals("/")) {
                List<Course> Courses = service.findAll();
                JSONObject outGoingDTO = CourseDTOMapper.mapAll(Courses);
                response = outGoingDTO.toJSONString();
            } else {
                Long id = Long.parseLong(info.substring(1));
                Course Course = service.findById(id);
                JSONObject object = CourseDTOMapper.map(Course);
                response = object.toJSONString();
            }
        } catch (ServiceException | NumberFormatException exception) {
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
        if (info == null || info.equals("/")) {
            try {
                BufferedReader reader = req.getReader();
                String jsonString = reader.lines().reduce(String::concat).get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
                Course course = CourseDTOMapper.map(jsonObject);
                service.save(course);
            } catch (ServiceException | ParseException exception) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info != null && info.matches("/\\d+")) {
            try {
                BufferedReader reader = req.getReader();
                String jsonString = reader.lines().reduce(String::concat).get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
                Course course = CourseDTOMapper.map(jsonObject);
                service.update(course);
            } catch (ServiceException | ParseException ex) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info != null && info.matches("/\\d+")) {
            try {
                Long id = Long.parseLong(info.substring(1));
                service.deleteById(id);
            } catch (ServiceException | NumberFormatException ex) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }
}
