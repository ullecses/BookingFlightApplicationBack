package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.Ticket;

import java.util.List;

public class TicketDao {

    private final EntityManager entityManager;

    public TicketDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Ticket create(Ticket ticket) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(ticket);
            transaction.commit();
            return ticket;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public Ticket findById(Long id) {
        return entityManager.find(Ticket.class, id);
    }

    public List<Ticket> findAll() {
        return entityManager.createQuery("SELECT t FROM Ticket t", Ticket.class).getResultList();
    }

    public Ticket update(Ticket ticket) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ticket updatedTicket = entityManager.merge(ticket);
            transaction.commit();
            return updatedTicket;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Ticket ticket = entityManager.find(Ticket.class, id);
            if (ticket != null) {
                entityManager.remove(ticket);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}

