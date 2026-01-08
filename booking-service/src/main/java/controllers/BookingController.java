package controllers;

import models.Booking;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import services.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> req) {
        Long userId = Long.parseLong(jwt.getSubject());
        Long roomId = Long.valueOf(req.get("roomId"));
        LocalDate startDate = LocalDate.parse(req.get("startDate"));
        LocalDate endDate = LocalDate.parse(req.get("endDate"));
        return bookingService.createBooking(userId, roomId, startDate, endDate);
    }

    @GetMapping
    public List<Booking> getBookings(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return bookingService.getBookings(userId);
    }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }
}
