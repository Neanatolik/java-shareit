package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                booking.getBooker(),
                booking.getStatus());
    }

    public Booking fromBookingDto(BookingDto bookingDto, long id) {
        return new Booking(id,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }

    public BookingDtoSend toBookingDtoSend(Booking booking, ItemDtoForBooking itemDtoForBooking, UserDto userDto) {
        return new BookingDtoSend(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemDtoForBooking(itemDtoForBooking.getId(), itemDtoForBooking.getName()),
                userDto,
                booking.getStatus());
    }

    public BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker());
    }

}
