package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.memory.InMemoryItemImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.memory.InMemoryUserImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final InMemoryItemImpl inMemoryItem;
    @Autowired
    private final InMemoryUserImpl inMemoryUser;

    private Long nextId = 0L;

    @Override
    public ItemDto post(Item item, Long userId) {
        checkItemsUser(userId);
        System.out.println(userId);
        checkItem(item);
        item.setId(getNextId());
        item.setOwner(userId);
        return ItemMapper.toItemDto(inMemoryItem.add(item));
    }

    @Override
    public ItemDto patch(Item item, Long itemId, Long userId) {
        checkItemsUser(userId);
        checkBelong(itemId, userId);
        return ItemMapper.toItemDto(inMemoryItem.patch(updateItem(item, inMemoryItem.getItem(itemId))));

    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        checkItemsUser(userId);
        return inMemoryItem.getItems(userId);
    }

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(inMemoryItem.getItem(id));
    }

    @Override
    public List<ItemDto> search(String itemName) {
        if (itemName.isBlank()) return new ArrayList<>();
        return inMemoryItem.search(itemName);
    }

    private void checkBelong(Long itemId, Long userId) {
        Long itemFromId = inMemoryItem.getItem(itemId).getOwner();
        if (!Objects.equals(itemFromId, userId)) {
            throw new NotFoundException(String.format("User %d doesn't have item %d", userId, itemId));
        }
    }

    private Item updateItem(Item itemNew, Item itemOld) {
        if (Objects.nonNull(itemNew.getAvailable())) itemOld.setAvailable(itemNew.getAvailable());
        if (Objects.nonNull(itemNew.getName())) itemOld.setName(itemNew.getName());
        if (Objects.nonNull(itemNew.getDescription())) itemOld.setDescription(itemNew.getDescription());
        return itemOld;
    }

    private void checkItemsUser(Long userId) {
        if (!inMemoryUser.getIds().contains(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

    private void checkItem(Item item) {
        if (Objects.isNull(item.getAvailable())) {
            throw new BadRequest("Item without available");
        } else if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            throw new BadRequest("Item without name");
        } else if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            throw new BadRequest("Item without description");
        }
    }

    private Long getNextId() {
        return ++nextId;
    }

}
