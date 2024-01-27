package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item, BookingDtoForItem lastBooking, BookingDtoForItem nextBooking, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments);
    }

    public ItemDtoForBooking toItemDtoForBooking(Item item) {
        return new ItemDtoForBooking(
                item.getId(),
                item.getName());
    }

    public Item fromItemDto(ItemDto itemDto, long id, User user) {
        return new Item(
                id,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user
        );
    }
}
