package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.UserController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoSend post(@RequestBody @Valid BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST (user: {}) /bookings", userId);
        return bookingService.post(bookingDto, userId);
    }

    @GetMapping
    public List<BookingDtoSend> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET (user: {}) /bookings?state={}", userId, state);
        return bookingService.get(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoSend patch(@PathVariable Long bookingId,
                                @RequestParam("approved") boolean approved,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH (user: {}) /bookings/{}?approved={}", userId, bookingId, approved);
        return bookingService.patch(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoSend get(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET (user: {}) /bookings/{}", userId, bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoSend> getOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET (user: {}) /owner?state={}", userId, state);
        return bookingService.getOwnersItem(userId, state);
    }

}
