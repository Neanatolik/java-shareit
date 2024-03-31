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
                comments,
                item.getRequestId());
    }

    public ItemDtoForBooking toItemDtoForBooking(Item item) {
        return new ItemDtoForBooking(
                item.getId(),
                item.getName());
    }

    public Item fromItemDto(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public Item fromItemDtoPost(ItemDtoPost itemDtoPost, User user) {
        Item item = new Item();
        item.setName(itemDtoPost.getName());
        item.setDescription(itemDtoPost.getDescription());
        item.setAvailable(itemDtoPost.getAvailable());
        item.setOwner(user);
        item.setRequestId(itemDtoPost.getRequestId());
        return item;
    }
}
