package ru.practicum.shareit.item.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
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
    public Item getItem(long id) {
        return itemHashMap.get(id);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemHashMap.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String itemName) {
        return itemHashMap.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(itemName.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
