package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest fromItemRequestDtoPost(ItemRequestDtoPost itemRequestDto, User user, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(itemRequestDto.getRequestor());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtoList.add(toItemRequestDto(itemRequest));
        }
        return dtoList;
    }

}
