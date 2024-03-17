package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveItemRequest(ItemRequestDtoPost itemRequestDtoPost, Long userId);

    List<ItemRequestDto> getItemRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestsByOwnerById(Long userId, Long itemRequestId);
}
