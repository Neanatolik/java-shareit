package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto item, long userId);

    ItemDto changeItem(ItemDto item, long id, long userId);

    List<ItemDto> getItemsByUserId(long userId);

    ItemDto getItemByItemAndUserId(long id, long userId);

    List<ItemDto> searchByItemName(String itemName, long userId);

    CommentDto postComment(long userId, Long itemId, Comment comment);
}
