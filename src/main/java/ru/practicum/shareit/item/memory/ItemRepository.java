package ru.practicum.shareit.item.memory;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item add(Item item);

    Item patch(Item item);

    Item getItem(long id);

    List<ItemDto> getItems(long userId);

    List<ItemDto> search(String itemName);
}
