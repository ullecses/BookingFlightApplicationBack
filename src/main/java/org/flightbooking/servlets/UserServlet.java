package org.flightbooking.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.flightbooking.HibernateUtil;
import org.flightbooking.models.Flight;
import org.flightbooking.models.User;
import org.flightbooking.services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for handling user-related operations:
 * - Fetching all users or a user by ID (GET)
 * - Creating a new user (POST)
 * - Updating user data (PUT)
 * - Updating user data (PATCH)
 * - Deleting a user (DELETE)
 */
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());
    private UserService userService;
    private ObjectMapper objectMapper;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Initializes required services and dependencies.
     */
    @Override
    public void init() throws ServletException {
        LOGGER.info("Initializing UserServlet...");
        EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager();
        objectMapper = new ObjectMapper(); // For JSON processing
        userService = new UserService(entityManager);
    }

    /**
     * Handles GET requests to fetch users.
     * If no path info is provided, all users are fetched.
     * If an ID is provided in the path, fetches a user by ID.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing GET request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Fetch all users
                List<User> users = userService.getAllUsers();
                LOGGER.info("Fetched all users: " + users.size());
                out.print(objectMapper.writeValueAsString(users));
            } else {
                // Fetch a user by ID
                Long userId = extractIdFromPath(pathInfo);
                if (userId == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid user ID\"}");
                    LOGGER.warning("Invalid user ID in path: " + pathInfo);
                    return;
                }
                User user = userService.getUserById(userId);
                if (user != null) {
                    LOGGER.info("Fetched user with ID: " + userId);
                    out.print(objectMapper.writeValueAsString(user));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"User not found\"}");
                    LOGGER.warning("User not found with ID: " + userId);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing GET request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles POST requests to create a new user.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing POST request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            User user = objectMapper.readValue(req.getReader(), User.class);
            User createdUser = userService.createUser(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(createdUser));
            LOGGER.info("Created new user: " + createdUser.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing POST request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles PUT requests to update user data.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing PUT request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            User user = objectMapper.readValue(req.getReader(), User.class);
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                LOGGER.info("Updated user: " + updatedUser.getId());
                out.print(objectMapper.writeValueAsString(updatedUser));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"User not found\"}");
                LOGGER.warning("User not found for update: " + user.getId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing PUT request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles HTTP PATCH requests to partially update a user.
     */
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing PATCH request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Flight ID is required\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 2 || pathParts[1].isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid Flight ID\"}");
                return;
            }

            Long userId = Long.parseLong(pathParts[1]);

            Map<String, Object> updates = objectMapper.readValue(req.getReader(), new TypeReference<Map<String, Object>>() {});

            User updatedUser = userService.updateUserPartial(userId, updates);

            if (updatedUser != null) {
                LOGGER.info("Partially updated flight: " + updatedUser.getId());
                out.print(objectMapper.writeValueAsString(updatedUser));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Flight not found\"}");
                LOGGER.warning("Flight not found for partial update: " + userId);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid Flight ID format\"}");
            LOGGER.warning("Invalid Flight ID format in PATCH request");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing PATCH request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles DELETE requests to remove a user by ID.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing DELETE request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            Long userId = extractIdFromPath(pathInfo);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid user ID\"}");
                LOGGER.warning("Invalid user ID in DELETE path: " + pathInfo);
                return;
            }
            userService.deleteUser(userId);
            LOGGER.info("Deleted user with ID: " + userId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing DELETE request", e);
            handleException(resp, e);
        }
    }

    /**
     * Extracts user ID from the path.
     */
    private Long extractIdFromPath(String pathInfo) {
        try {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                return Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid ID format in path: " + pathInfo);
        }
        return null;
    }

    /**
     * Handles exceptions and sends an appropriate response.
     */
    private void handleException(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().print("{\"error\":\"An error occurred: " + e.getMessage() + "\"}");
        LOGGER.log(Level.SEVERE, "Unhandled exception", e);
    }

    /**
     * Cleans up resources when the servlet is destroyed.
     */
    @Override
    public void destroy() {
        LOGGER.info("Destroying UserServlet...");
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}