package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.Flight;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing Flight entities.
 * Provides CRUD operations and partial update functionality for flights.
 */
public class FlightDao {

    private static final Logger LOGGER = Logger.getLogger(FlightDao.class.getName());
    private final EntityManager entityManager;

    /**
     * Constructs a FlightDao with the given EntityManager.
     *
     * @param entityManager the EntityManager to manage persistence.
     */
    public FlightDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new flight in the database.
     *
     * @param flight the Flight entity to persist.
     * @return the persisted Flight entity.
     */
    public Flight create(Flight flight) {
        LOGGER.info("Creating new flight: " + flight);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(flight);
            transaction.commit();
            LOGGER.info("Flight created successfully: " + flight.getId());
            return flight;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error creating flight: " + flight, e);
            throw e;
        }
    }

    /**
     * Finds a flight by its ID.
     *
     * @param id the ID of the flight.
     * @return the Flight entity if found, or null if not found.
     */
    public Flight findById(Long id) {
        LOGGER.info("Finding flight by ID: " + id);
        return entityManager.find(Flight.class, id);
    }

    /**
     * Retrieves all flights from the database.
     *
     * @return a list of all Flight entities.
     */
    public List<Flight> findAll() {
        LOGGER.info("Retrieving all flights from the database...");
        return entityManager.createQuery("SELECT f FROM Flight f", Flight.class).getResultList();
    }

    /**
     * Updates an existing flight in the database.
     *
     * @param flight the Flight entity with updated data.
     * @return the updated Flight entity.
     */
    public Flight update(Flight flight) {
        LOGGER.info("Updating flight: " + flight.getId());
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Flight updatedFlight = entityManager.merge(flight);
            transaction.commit();
            LOGGER.info("Flight updated successfully: " + flight.getId());
            return updatedFlight;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error updating flight: " + flight.getId(), e);
            throw e;
        }
    }

    /**
     * Partially updates a flight in the database.
     *
     * @param flightId the ID of the flight to update.
     * @param updates  a map of field names and their new values.
     * @return the updated Flight entity, or null if not found.
     */
    public Flight updatePartial(Long flightId, Map<String, Object> updates) {
        LOGGER.info("Partially updating flight: " + flightId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Flight flight = entityManager.find(Flight.class, flightId);
            if (flight == null) {
                LOGGER.warning("Flight not found for partial update: " + flightId);
                transaction.rollback();
                return null;
            }

            updates.forEach((key, value) -> {
                try {
                    if (!"id".equalsIgnoreCase(key)) {
                        Field field = Flight.class.getDeclaredField(key);
                        field.setAccessible(true);

                        if (field.getType().equals(LocalDateTime.class)) {
                            field.set(flight, LocalDateTime.parse(value.toString()));
                        } else {
                            field.set(flight, value);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    LOGGER.warning("Unknown field in partial update: " + key);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LOGGER.warning("Failed to update field: " + key + " due to " + e.getMessage());
                }
            });

            Flight updatedFlight = entityManager.merge(flight);
            transaction.commit();
            LOGGER.info("Flight partially updated successfully: " + flightId);
            return updatedFlight;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error partially updating flight: " + flightId, e);
            throw e;
        }
    }

    /**
     * Deletes a flight from the database by its ID.
     *
     * @param id the ID of the flight to delete.
     */
    public void delete(Long id) {
        LOGGER.info("Deleting flight with ID: " + id);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Flight flight = entityManager.find(Flight.class, id);
            if (flight != null) {
                entityManager.remove(flight);
                LOGGER.info("Flight deleted successfully: " + id);
            } else {
                LOGGER.warning("Flight not found for deletion: " + id);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error deleting flight with ID: " + id, e);
            throw e;
        }
    }
}
