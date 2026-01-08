package repositories;

import models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    public List<Room> findByAvailable(boolean available);
}