package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

public class MainPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        StringBuilder builder = new StringBuilder();
        String[] files = {"src/main/resources/index.html"};
        for (String file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.lines().forEach(builder::append);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Writer writer = resp.getWriter();
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
}
