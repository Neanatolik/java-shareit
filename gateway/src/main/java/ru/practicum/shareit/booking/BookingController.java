package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestBody @Valid BookingDto bookingDto, @RequestHeader(user) long userId) {
        log.info("POST (user: {}) /bookings", userId);
        return bookingClient.saveBooking(bookingDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingWithState(@RequestHeader(user) long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("GET (user: {}) /bookings?state={}&from={}&size={}", userId, state, from, size);
        return bookingClient.getBookingWithState(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBooking(@PathVariable Long bookingId,
                                                @RequestParam("approved") boolean approved,
                                                @RequestHeader(user) long userId) {
        log.info("PATCH (user: {}) /bookings/{}?approved={}", userId, bookingId, approved);
        return bookingClient.changeBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingWithoutState(@PathVariable long bookingId, @RequestHeader(user) long userId) {
        log.info("GET (user: {}) /bookings/{}", userId, bookingId);
        return bookingClient.getBookingWithoutState(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnersItem(@RequestHeader(user) long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET (user: {}) /owner?state={}&from={}&size={}", userId, state, from, size);
        return bookingClient.getOwnersItem(userId, state, from, size);
    }

}
