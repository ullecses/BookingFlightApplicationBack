package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.Flight;

import java.util.List;

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

