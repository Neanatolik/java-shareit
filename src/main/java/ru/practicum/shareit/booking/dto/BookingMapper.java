package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker().getId());
    }

    public Booking fromBookingDto(BookingDto bookingDto, User user, Item item, Long id) {
        return new Booking(id,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus());
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
