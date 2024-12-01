package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.BookingDao;
import org.flightbooking.models.Booking;
import org.flightbooking.models.Ticket;
import org.flightbooking.models.User;

import java.util.List;

public class BookingService {

    private final BookingDao bookingDao;

    public BookingService(EntityManager entityManager) {
        this.bookingDao = new BookingDao(entityManager);
    }

    public Booking createBooking(User user, Ticket ticket) {
        Booking booking = new Booking(null, user, ticket);
        return bookingDao.create(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingDao.findById(id);
    }

    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    public Booking updateBooking(Booking booking) {
        return bookingDao.update(booking);
    }

    public void deleteBooking(Long id) {
        bookingDao.delete(id);
    }
}
