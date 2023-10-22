package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto post(Item item, Long userId);

    ItemDto patch(Item item, Long id, Long userId);

    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Long id);

    List<ItemDto> search(String itemName);
}
