package services;

import models.Hotel;
import models.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.HotelRepository;
import repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    public Hotel addHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }
    public Optional<Hotel> getHotel(Long id) {
        return hotelRepository.findById(id);
    }

    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }
    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailable(true);
    }
    public List<Room> getRecommendedRooms() {
        return roomRepository.findByAvailable(true).stream()
                .sorted(Comparator.comparingLong(Room::getTimesBooked).reversed())
                .toList();
    }

    public Boolean confirmAvailability(Long roomId, String traceId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            logger.error("{}: Room not found", traceId);
            return false;
        }
        boolean isAvailable = room.available;
        if (!isAvailable) {
            logger.error("{}: Room {} is not available", traceId, roomId);
        }
        return isAvailable;
    }
}
