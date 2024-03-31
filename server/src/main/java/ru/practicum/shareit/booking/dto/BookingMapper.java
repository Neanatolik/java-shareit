package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker().getId());
    }

    public Booking fromBookingDto(BookingDto bookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDtoSend toBookingDtoSend(Booking booking) {
        return new BookingDtoSend(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDtoForBooking(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}
