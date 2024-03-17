package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDtoPost item, long userId);

    ItemDto changeItem(ItemDtoPost item, long id, long userId);

    List<ItemDto> getItemsByUserId(long userId);

    ItemDto getItemByItemAndUserId(long id, long userId);

    List<ItemDto> searchByItemName(String itemName, long userId, int from, int size);

    CommentDto postComment(long userId, Long itemId, CommentDtoPost comment);
}
