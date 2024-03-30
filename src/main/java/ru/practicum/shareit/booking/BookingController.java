package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoSend saveBooking(@RequestBody @Valid BookingDto bookingDto, @RequestHeader(user) long userId) {
        log.info("POST (user: {}) /bookings", userId);
        return bookingService.saveBooking(bookingDto, userId);
    }

    @GetMapping
    public List<BookingDtoSend> getBookingWithState(@RequestHeader(user) long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("GET (user: {}) /bookings?state={}&from={}&size={}", userId, state, from, size);
        return bookingService.getBookingWithState(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoSend changeBooking(@PathVariable Long bookingId,
                                        @RequestParam("approved") boolean approved,
                                        @RequestHeader(user) long userId) {
        log.info("PATCH (user: {}) /bookings/{}?approved={}", userId, bookingId, approved);
        return bookingService.changeBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoSend getBookingWithoutState(@PathVariable long bookingId, @RequestHeader(user) long userId) {
        log.info("GET (user: {}) /bookings/{}", userId, bookingId);
        return bookingService.getBookingWithoutState(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoSend> getOwnersItem(@RequestHeader(user) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("GET (user: {}) /owner?state={}&from={}&size={}", userId, state, from, size);
        return bookingService.getOwnersItem(userId, state, from, size);
    }

}
