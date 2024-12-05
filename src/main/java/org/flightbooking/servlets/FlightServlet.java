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
import org.flightbooking.services.FlightService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for handling flight-related operations:
 * - Fetching all flights or a flight by ID (GET)
 * - Creating a new flight (POST)
 * - Updating flight data (PUT)
 * - Deleting a flight (DELETE)
 */
@WebServlet("/flights/*")
public class FlightServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FlightServlet.class.getName());
    private FlightService flightService;
    private ObjectMapper objectMapper;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Initializes required services and dependencies.
     */
    @Override
    public void init() throws ServletException {
        LOGGER.info("Initializing FlightServlet...");
        EntityManager entityManager = HibernateUtil.getSessionFactory().createEntityManager();
        objectMapper = new ObjectMapper(); // For JSON processing
        flightService = new FlightService(entityManager);
    }

    /**
     * Handles GET requests to fetch flights.
     * If no path info is provided, all flights are fetched.
     * If an ID is provided in the path, fetches a flight by ID.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing GET request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Fetch all flights
                List<Flight> flights = flightService.getAllFlights();
                LOGGER.info("Fetched all flights: " + flights.size());
                out.print(objectMapper.writeValueAsString(flights));
            } else {
                // Fetch a flight by ID
                Long flightId = extractIdFromPath(pathInfo);
                if (flightId == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid flight ID\"}");
                    LOGGER.warning("Invalid flight ID in path: " + pathInfo);
                    return;
                }
                Flight flight = flightService.getFlightById(flightId);
                if (flight != null) {
                    LOGGER.info("Fetched flight with ID: " + flightId);
                    out.print(objectMapper.writeValueAsString(flight));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Flight not found\"}");
                    LOGGER.warning("Flight not found with ID: " + flightId);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing GET request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles POST requests to create a new flight.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing POST request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            Flight flight = objectMapper.readValue(req.getReader(), Flight.class);
            Flight createdFlight = flightService.createFlight(flight);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(createdFlight));
            LOGGER.info("Created new flight: " + createdFlight.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing POST request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles PUT requests to update flight data.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing PUT request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            Flight flight = objectMapper.readValue(req.getReader(), Flight.class);
            Flight updatedFlight = flightService.updateFlight(flight);
            if (updatedFlight != null) {
                LOGGER.info("Updated flight: " + updatedFlight.getId());
                out.print(objectMapper.writeValueAsString(updatedFlight));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Flight not found\"}");
                LOGGER.warning("Flight not found for update: " + flight.getId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing PUT request", e);
            handleException(resp, e);
        }
    }

    /**
     * Handles DELETE requests to remove a flight by ID.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing DELETE request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            Long flightId = extractIdFromPath(pathInfo);
            if (flightId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid flight ID\"}");
                LOGGER.warning("Invalid flight ID in DELETE path: " + pathInfo);
                return;
            }
            flightService.deleteFlight(flightId);
            LOGGER.info("Deleted flight with ID: " + flightId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing DELETE request", e);
            handleException(resp, e);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Processing PATCH request...");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            // Чтение ID из пути запроса
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

            Long flightId = Long.parseLong(pathParts[1]);

            Map<String, Object> updates = objectMapper.readValue(req.getReader(), new TypeReference<Map<String, Object>>() {});

            Flight updatedFlight = flightService.updateFlightPartial(flightId, updates);

            if (updatedFlight != null) {
                LOGGER.info("Partially updated flight: " + updatedFlight.getId());
                out.print(objectMapper.writeValueAsString(updatedFlight));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Flight not found\"}");
                LOGGER.warning("Flight not found for partial update: " + flightId);
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
     * Extracts flight ID from the path.
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
