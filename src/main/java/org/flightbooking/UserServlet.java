package org.flightbooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.flightbooking.dao.UserDao;
import org.flightbooking.models.User;
import org.flightbooking.services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void init() throws ServletException {
        EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager();
        objectMapper = new ObjectMapper(); // Для работы с JSON
        userService = new UserService(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Получение всех пользователей
                List<User> users = userService.getAllUsers();
                out.print(objectMapper.writeValueAsString(users));
            } else {
                // Получение пользователя по ID
                Long userId = extractIdFromPath(pathInfo);
                if (userId == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid user ID\"}");
                    return;
                }
                User user = userService.getUserById(userId);
                if (user != null) {
                    out.print(objectMapper.writeValueAsString(user));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"User not found\"}");
                }
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            User user = objectMapper.readValue(req.getReader(), User.class);
            User createdUser = userService.createUser(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(createdUser));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            User user = objectMapper.readValue(req.getReader(), User.class);
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                out.print(objectMapper.writeValueAsString(updatedUser));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"User not found\"}");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            Long userId = extractIdFromPath(pathInfo);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid user ID\"}");
                return;
            }
            userService.deleteUser(userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"User not found\"}");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void destroy() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    private Long extractIdFromPath(String pathInfo) {
        try {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                return Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }

    private void handleException(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().print("{\"error\":\"An error occurred: " + e.getMessage() + "\"}");
        e.printStackTrace();
    }
}
