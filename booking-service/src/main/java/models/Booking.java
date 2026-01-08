package models;

import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long userId;
    public Long roomId;

    public LocalDate startDate;
    public LocalDate endDate;

    @Enumerated(EnumType.STRING)
    public BookingStatus status;

    public OffsetDateTime createdAt;

    public String paymentMethod = "ON_SITE";
}
