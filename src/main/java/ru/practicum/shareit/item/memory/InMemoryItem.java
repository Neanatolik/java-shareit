package ru.practicum.shareit.item.memory;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface InMemoryItem {

    Item add(Item item);

    Item patch(Item item);

    Item getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String itemName);
}
