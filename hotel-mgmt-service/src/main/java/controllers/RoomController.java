package controllers;

import models.Room;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import services.HotelService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final HotelService hotelService;

    public RoomController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public Room addRoom(@RequestBody Room room) {
        return hotelService.addRoom(room);
    }

    @GetMapping("/recommended")
    public List<Room> getRecommendedRooms() {
        return hotelService.getRecommendedRooms();
    }

    @GetMapping
    public List<Room> getAvailableRooms() {
        return hotelService.getAvailableRooms();
    }

    @PostMapping("/{id}/confirm-availability")
    public Boolean confirmAvailability(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        String traceId = headers.get("x-trace-id");
        return hotelService.confirmAvailability(id, traceId);
    }
}
