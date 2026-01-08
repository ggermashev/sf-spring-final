package models;

import jakarta.persistence.*;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    public Hotel hotel;

    public String number;

    public Boolean available = true;

    public Long timesBooked;

    public Long seats;

    public Long getTimesBooked() {
        return timesBooked;
    }
}
