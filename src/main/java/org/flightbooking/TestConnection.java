package org.flightbooking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/FlightBooking",
                    "builder",
                    "656565"
            );
            System.out.println("Connection successful");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
