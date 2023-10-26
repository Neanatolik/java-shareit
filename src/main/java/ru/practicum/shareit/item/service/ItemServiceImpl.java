package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.memory.ItemRepositoryImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.memory.UserRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl inMemoryItem;
    private final UserRepositoryImpl inMemoryUser;
    private long nextId = 0L;


    @Autowired
    public ItemServiceImpl(ItemRepositoryImpl inMemoryItem, UserRepositoryImpl inMemoryUser) {
        this.inMemoryItem = inMemoryItem;
        this.inMemoryUser = inMemoryUser;
    }

    @Override
    public ItemDto post(ItemDto item, long userId) {
        checkItemsUser(userId);
        checkItem(item);
        return ItemMapper.toItemDto(inMemoryItem.add(ItemMapper.fromItemDto(item, getNextId(), userId)));
    }

    @Override
    public ItemDto patch(ItemDto item, long itemId, long userId) {
        checkItemsUser(userId);
        checkBelong(itemId, userId);
        return ItemMapper.toItemDto(inMemoryItem.patch(updateItem(ItemMapper.fromItemDto(item, itemId, userId), inMemoryItem.getItem(itemId))));

    }

    @Override
    public List<ItemDto> getItems(long userId) {
        checkItemsUser(userId);
        return inMemoryItem.getItems(userId);
    }

    @Override
    public ItemDto getItem(long id) {
        return ItemMapper.toItemDto(inMemoryItem.getItem(id));
    }

    @Override
    public List<ItemDto> search(String itemName) {
        if (itemName.isBlank()) return Collections.emptyList();
        return inMemoryItem.search(itemName);
    }

    private void checkBelong(long itemId, long userId) {
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

    private void checkItemsUser(long userId) {
        if (!inMemoryUser.getIds().contains(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

    private void checkItem(ItemDto item) {
        if (Objects.isNull(item.getAvailable())) {
            throw new BadRequest("Item without available");
        } else if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            throw new BadRequest("Item without name");
        } else if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            throw new BadRequest("Item without description");
        }
    }

    private long getNextId() {
        return ++nextId;
    }

}
