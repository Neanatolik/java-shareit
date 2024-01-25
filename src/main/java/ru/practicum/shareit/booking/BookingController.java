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
    public BookingDtoSend post(@RequestBody @Valid BookingDto bookingDto, @RequestHeader(user) long userId) {
        log.info("POST (user: {}) /bookings", userId);
        return bookingService.post(bookingDto, userId);
    }

    @GetMapping
    public List<BookingDtoSend> get(@RequestHeader(user) long userId,
                                    @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET (user: {}) /bookings?state={}", userId, state);
        return bookingService.get(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoSend patch(@PathVariable Long bookingId,
                                @RequestParam("approved") boolean approved,
                                @RequestHeader(user) long userId) {
        log.info("PATCH (user: {}) /bookings/{}?approved={}", userId, bookingId, approved);
        return bookingService.patch(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoSend get(@PathVariable long bookingId, @RequestHeader(user) long userId) {
        log.info("GET (user: {}) /bookings/{}", userId, bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoSend> getOwner(@RequestHeader(user) long userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET (user: {}) /owner?state={}", userId, state);
        return bookingService.getOwnersItem(userId, state);
    }

}
