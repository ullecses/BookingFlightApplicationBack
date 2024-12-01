package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.FlightDao;
import org.flightbooking.models.Flight;

import java.util.List;

public class FlightService {

    private final FlightDao flightDao;

    public FlightService(EntityManager entityManager) {
        this.flightDao = new FlightDao(entityManager);
    }

    public Flight createFlight(int flightNumber, String origin, String destination, double price, String departureTime, String arrivalTime) {
        Flight flight = new Flight(null, flightNumber, origin, destination, price, departureTime, arrivalTime);
        return flightDao.create(flight);
    }

    public Flight getFlightById(Long id) {
        return flightDao.findById(id);
    }

    public List<Flight> getAllFlights() {
        return flightDao.findAll();
    }

    public Flight updateFlight(Flight flight) {
        return flightDao.update(flight);
    }

    public void deleteFlight(Long id) {
        flightDao.delete(id);
    }
}
