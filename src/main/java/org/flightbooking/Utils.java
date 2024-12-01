package org.flightbooking;

public class Utils {
    public enum TicketStatus {
        FREE, // Билет доступен для покупки
        BOOKED, // Билет забронирован
        PURCHASED // Билет куплен
    }

    public enum UserRole {
        ADMIN,
        CUSTOMER
    }
}
