package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto saveItemRequest(@RequestHeader(user) Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST (user: {}) /requests", userId);
        return itemRequestService.saveItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequest(@RequestHeader(user) Long userId) {
        log.info("GET (user: {}) /requests", userId);
        return itemRequestService.getItemRequestsByOwner(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(user) Long userId,
                                                   @PathVariable Long itemRequestId) {
        log.info("GET (user: {}) /requests/{}", userId, itemRequestId);
        return itemRequestService.getItemRequestsByOwnerById(userId, itemRequestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestsPage(@RequestHeader(user) Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("POST (user: {}) /requests/all?from={}&size={}", userId, from, size);
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

}
