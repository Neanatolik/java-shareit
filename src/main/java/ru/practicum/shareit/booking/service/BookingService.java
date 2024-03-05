package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;

import java.util.List;

public interface BookingService {
    BookingDtoSend saveBooking(BookingDto bookingDto, long userId);

    BookingDtoSend changeBooking(long bookingId, boolean approved, long userId);

    BookingDtoSend getBookingWithoutState(long bookingId, long userId);

    List<BookingDtoSend> getBookingWithState(long userId, String state, int from, int size);

    List<BookingDtoSend> getOwnersItem(long userId, String state, int from, int size);
}
