package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestsByOwnerById(Long userId, Long itemRequestId);
}
