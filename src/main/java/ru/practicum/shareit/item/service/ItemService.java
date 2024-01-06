package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto post(ItemDto item, long userId);

    ItemDto patch(ItemDto item, long id, long userId);

    List<ItemDto> getItems(long userId);

    ItemDto getItem(long id);

    List<ItemDto> search(String itemName);
}
