package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.TicketDao;
import org.flightbooking.models.Flight;
import org.flightbooking.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private final TicketDao ticketDao;

    public TicketService(EntityManager entityManager) {
        this.ticketDao = new TicketDao(entityManager);
    }

    public Ticket createTicket(Flight flight, int seatNumber) {
        Ticket ticket = new Ticket(null, flight, seatNumber, null);
        return ticketDao.create(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketDao.findById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketDao.findAll();
    }

    public List<Ticket> createTickets(Flight flight, int numberOfSeats) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= numberOfSeats; i++) {
            Ticket ticket = new Ticket(null, flight, i, null);
            tickets.add(ticketDao.create(ticket));
        }
        return tickets;
    }

    public Ticket updateTicket(Ticket ticket) {
        return ticketDao.update(ticket);
    }

    public void deleteTicket(Long id) {
        ticketDao.delete(id);
    }
}
