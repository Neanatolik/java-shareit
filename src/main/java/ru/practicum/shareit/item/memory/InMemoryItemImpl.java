package ru.practicum.shareit.item.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemImpl implements InMemoryItem {
    private final HashMap<Long, Item> itemHashMap;

    @Override
    public Item add(Item item) {
        itemHashMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patch(Item item) {
        itemHashMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        return itemHashMap.get(id);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemHashMap.values().
                stream().
                filter(item -> item.getOwner().equals(userId)).
                map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String itemName) {
        return itemHashMap.values().
                stream().
                filter(item -> item.getDescription().toLowerCase().contains(itemName.toLowerCase())).
                filter(Item::getAvailable).
                map(ItemMapper::toItemDto).
                collect(Collectors.toList());
    }
}
