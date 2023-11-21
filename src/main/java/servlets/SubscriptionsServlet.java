package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Subscription;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.impl.ServiceException;
import service.impl.SubscriptionService;
import servlets.mapper.SubscriptionDTOMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class SubscriptionsServlet extends HttpServlet {
    private SubscriptionService service;
    private JSONParser jsonParser = new JSONParser();

    public SubscriptionsServlet(SubscriptionService service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String info = req.getPathInfo();
        String response = "";
        if (info == null || info.equals("/")) {
            List<Subscription> Subscriptions = service.findAll();
            JSONObject outGoingDTO = SubscriptionDTOMapper.mapAll(Subscriptions);
            response = outGoingDTO.toJSONString();
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");
            Writer writer = resp.getWriter();
            writer.write(response);
            writer.flush();
            writer.close();
        } else {
            resp.sendError(404);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) {
            try {
                BufferedReader reader = req.getReader();
                String jsonString = reader.lines().reduce(String::concat).get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
                Subscription Subscription = SubscriptionDTOMapper.map(jsonObject);
                service.save(Subscription);
            } catch (ServiceException | ParseException exception) {
                resp.sendError(400);
            }
        } else {
            resp.sendError(404);
        }
    }
}
