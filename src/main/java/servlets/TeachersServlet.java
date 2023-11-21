package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Teacher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.impl.ServiceException;
import service.impl.TeacherService;
import servlets.mapper.TeacherDTOMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TeachersServlet extends HttpServlet {
    private TeacherService service;
    private JSONParser jsonParser = new JSONParser();

    public TeachersServlet(TeacherService service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String info = req.getPathInfo();
        String response = "";
        try {
            if (info == null || info.equals("/")) {
                List<Teacher> teachers = service.findAll();
                JSONObject outGoingDTO = TeacherDTOMapper.mapAll(teachers);
                response = outGoingDTO.toJSONString();
            } else {
                Long id = Long.parseLong(info.substring(1));
                Teacher Teacher = service.findById(id);
                JSONObject object = TeacherDTOMapper.map(Teacher);
                response = object.toJSONString();
            }
        } catch (NumberFormatException | ServiceException exception) {
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
                Teacher Teacher = TeacherDTOMapper.map(jsonObject);
                service.save(Teacher);
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
                Teacher Teacher = TeacherDTOMapper.map(jsonObject);
                service.update(Teacher);
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
