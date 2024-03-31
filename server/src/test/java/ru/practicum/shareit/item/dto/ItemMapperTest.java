package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User user = new User(1L, "User", "User@mail.com");
        Item item = new Item(1L, "Item", "ItemDescription", true,
                user, 2L);
        BookingDtoForItem lastBooking = new BookingDtoForItem(1L, user.getId());
        BookingDtoForItem nextBooking = new BookingDtoForItem(2L, user.getId());
        ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking, null);
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(lastBooking.getBookerId()));
    }

    @Test
    void toItemDtoForBooking() {
        User user = new User(1L, "User", "User@mail.com");
        Item item = new Item(1L, "Item", "ItemDescription", true,
                user, 2L);
        ItemDtoForBooking itemDtoForBooking = ItemMapper.toItemDtoForBooking(item);
        assertThat(itemDtoForBooking.getName(), equalTo(item.getName()));

    }

    @Test
    void fromItemDto() {
        User user = new User(1L, "User", "User@mail.com");
        BookingDtoForItem lastBooking = new BookingDtoForItem(1L, user.getId());
        BookingDtoForItem nextBooking = new BookingDtoForItem(2L, user.getId());
        ItemDto itemDto = new ItemDto(1L, "ItemDto", "ItemDtoDescription", true, lastBooking, nextBooking, null, 2L);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        assertThat(item.getName(), equalTo(item.getName()));
    }

    @Test
    void fromItemDtoPost() {
        User user = new User(1L, "User", "User@mail.com");
        ItemDtoPost itemDtoPost = new ItemDtoPost("ItemDto", "ItemDtoDescription", true, 2L);
        Item item = ItemMapper.fromItemDtoPost(itemDtoPost, user);
        assertThat(item.getName(), equalTo(item.getName()));
    }
}