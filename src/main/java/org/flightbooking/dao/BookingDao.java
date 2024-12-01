package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.Booking;

import java.util.List;

public class BookingDao {

    private final EntityManager entityManager;

    public BookingDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Booking create(Booking booking) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(booking);
            transaction.commit();
            return booking;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public Booking findById(Long id) {
        return entityManager.find(Booking.class, id);
    }

    public List<Booking> findAll() {
        return entityManager.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
    }

    public Booking update(Booking booking) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Booking updatedBooking = entityManager.merge(booking);
            transaction.commit();
            return updatedBooking;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Booking booking = entityManager.find(Booking.class, id);
            if (booking != null) {
                entityManager.remove(booking);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
