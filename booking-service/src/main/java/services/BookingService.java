package services;

import errors.AlreadyBookedException;
import models.Booking;
import models.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import repositories.BookingRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final Duration TIMEOUT_MS = Duration.ofMillis(3000);
    private final int RETRY_COUNT = 1;

    private final BookingRepository bookingRepository;
    private final WebClient webClient;

    public BookingService(BookingRepository bookingRepository, WebClient hotelClient) {
        this.bookingRepository = bookingRepository;
        this.webClient = hotelClient;
    }

    @Transactional
    public Booking createBooking(
            Long userId,
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        boolean hasBookingsOnPeriod =
                bookings.stream().filter(b -> b.status != BookingStatus.CANCELLED &&
                        (b.endDate.isAfter(startDate) && b.endDate.isBefore(endDate) || b.startDate.isAfter(startDate) && b.startDate.isBefore(endDate))).toArray().length > 0;
        if (hasBookingsOnPeriod) {
            throw new AlreadyBookedException();
        }

        Booking booking = new Booking();
        booking.userId = userId;
        booking.roomId = roomId;
        booking.startDate = startDate;
        booking.endDate = endDate;
        booking.status = BookingStatus.PENDING;
        booking.createdAt = java.time.OffsetDateTime.now();

        booking = bookingRepository.save(booking);
        String traceId = UUID.randomUUID().toString();
        logger.info("{}: Booking created", traceId);

        try {
            Boolean roomAvailable = confirmRoomAvailable(roomId, traceId).block();
            if (Boolean.TRUE.equals(roomAvailable)) {
                booking.status = BookingStatus.CONFIRMED;
                bookingRepository.save(booking);
                logger.info("{}: Booking confirmed", traceId);
            } else {
                booking.status = BookingStatus.CANCELLED;
                bookingRepository.save(booking);
                logger.info("{}: Booking cancelled because room is not available", traceId);
            }
        } catch (Exception e) {
            booking.status = BookingStatus.CANCELLED;
            bookingRepository.save(booking);
            logger.info("{}: Booking cancelled because of internal error", traceId);
        }

        return booking;
    }

    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private Mono<Boolean> confirmRoomAvailable(Long roomId, String traceId) {
        String path = "/rooms/" + roomId + "/confirm-availability";

        return webClient.post()
                .uri(path)
                .header("x-trace-id", traceId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(TIMEOUT_MS)
                .retryWhen(Retry.backoff(RETRY_COUNT, Duration.ofMillis(300)).maxBackoff(Duration.ofSeconds(2)));
    }

}
