package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto saveItemRequest(ItemRequestDtoPost itemRequestDtoPost, Long userId) {
        checkItemRequestDescription(itemRequestDtoPost.getDescription());
        checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDtoPost(itemRequestDtoPost,
                userRepository.findById(userId).get(),
                LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByOwner(Long userId) {
        checkUser(userId);
        return setItemToItemRequestList(itemRepository.findAllItemByRequestIdIsNotNull(), ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findAllItemRequestByRequestor(userRepository.getReferenceById(userId))));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        checkFromAndSize(from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return setItemToItemRequestList(itemRepository.findAllItemByRequestIdIsNotNull(), ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.findAllItemRequest(userId, page)));
    }

    @Override
    public ItemRequestDto getItemRequestsByOwnerById(Long userId, Long itemRequestId) {
        checkUser(userId);
        checkItemRequestId(itemRequestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.getReferenceById(itemRequestId));
        List<Item> items = itemRepository.findAllItemByRequestId(itemRequestId);
        if (!items.isEmpty()) {
            itemRequestDto.setItems(items);
        }
        return itemRequestDto;
    }

    private List<ItemRequestDto> setItemToItemRequestList(List<Item> listOfItemsWithRequestId, List<ItemRequestDto> itemRequests) {
        if (listOfItemsWithRequestId.isEmpty()) return itemRequests;
        for (ItemRequestDto itemRequest : itemRequests) {
            List<Item> items = new ArrayList<>(itemRequest.getItems());
            for (Item item : listOfItemsWithRequestId) {
                if (Objects.equals(item.getRequestId(), itemRequest.getId())) {
                    items.add(item);
                }
            }
            itemRequest.setItems(items);
        }
        return itemRequests;
    }

    private void checkUser(Long userId) {
        if (!userRepository.existUserId(userId)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не сущетсвует", userId));
        }

    }

    private void checkItemRequestId(Long itemRequestId) {
        if (itemRequestRepository.existItemById(itemRequestId)) {
            throw new NotFoundException("Неверный id");
        }
    }

    private void checkFromAndSize(int from, int size) {
        if ((from < 0) || (size <= 0)) {
            throw new BadRequest("Неверные значения");
        }
    }

    private void checkItemRequestDescription(String description) {
        if (Objects.isNull(description) || description.isEmpty() || description.isBlank()) {
            throw new BadRequest("Нет описания");
        }
    }
}
