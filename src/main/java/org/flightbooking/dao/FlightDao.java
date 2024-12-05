package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.Flight;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FlightDao {

    private final EntityManager entityManager;

    public FlightDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Flight create(Flight flight) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(flight);
            transaction.commit();
            return flight;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public Flight findById(Long id) {
        return entityManager.find(Flight.class, id);
    }

    public List<Flight> findAll() {
        return entityManager.createQuery("SELECT f FROM Flight f", Flight.class).getResultList();
    }

    public Flight update(Flight flight) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Flight updatedFlight = entityManager.merge(flight);
            transaction.commit();
            return updatedFlight;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public Flight updatePartial(Long flightId, Map<String, Object> updates) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Найти существующий объект Flight
            Flight flight = entityManager.find(Flight.class, flightId);
            if (flight == null) {
                transaction.rollback();
                return null; // Если не найдено, возвращаем null
            }

            updates.forEach((key, value) -> {
                try {
                    if (!"id".equalsIgnoreCase(key)) { // Исключаем обновление id
                        Field field = Flight.class.getDeclaredField(key);
                        field.setAccessible(true);

                        if (field.getType().equals(LocalDateTime.class)) {
                            field.set(flight, LocalDateTime.parse(value.toString()));
                        } else {
                            field.set(flight, value);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    System.err.println("Unknown field: " + key);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    System.err.println("Failed to update field: " + key + ", reason: " + e.getMessage());
                }
            });

            Flight updatedFlight = entityManager.merge(flight);
            transaction.commit();
            return updatedFlight;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Flight flight = entityManager.find(Flight.class, id);
            if (flight != null) {
                entityManager.remove(flight);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}

