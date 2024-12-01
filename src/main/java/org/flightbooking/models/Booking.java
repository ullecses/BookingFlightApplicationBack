package org.flightbooking.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Связь с пользователем
    private User user;

    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false) // Связь с билетом
    private Ticket ticket;
}

