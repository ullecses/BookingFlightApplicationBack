package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.FlightDao;
import org.flightbooking.models.Flight;

import java.util.List;
import java.util.Map;

public class FlightService {

    private final FlightDao flightDao;

    public FlightService(EntityManager entityManager) {
        this.flightDao = new FlightDao(entityManager);
    }

    public Flight createFlight(Flight flight) {
        return flightDao.create(flight);
    }

    public Flight updateFlightPartial(Long flightId, Map<String, Object> updates) {
        return flightDao.updatePartial(flightId, updates);
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
