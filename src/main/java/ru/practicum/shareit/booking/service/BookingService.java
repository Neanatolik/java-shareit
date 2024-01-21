package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;

import java.util.List;

public interface BookingService {
    BookingDtoSend post(BookingDto bookingDto, long userId);

    BookingDtoSend patch(long bookingId, boolean approved, long userId);

    BookingDtoSend get(long bookingId, long userId);

    List<BookingDtoSend> get(long userId, String state);

    List<BookingDtoSend> getOwnersItem(long userId, String state);
}
