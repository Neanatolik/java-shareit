package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader(user) Long userId,
                                                  @RequestBody ItemRequestDtoPost itemRequestDtoPost) {
        log.info("POST (user: {}) /requests", userId);
        return itemRequestClient.saveItemRequest(itemRequestDtoPost, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequest(@RequestHeader(user) Long userId) {
        log.info("GET (user: {}) /requests", userId);
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(user) Long userId,
                                                     @PathVariable Long itemRequestId) {
        log.info("GET (user: {}) /requests/{}", userId, itemRequestId);
        return itemRequestClient.getItemRequestsByOwnerById(userId, itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsPage(@RequestHeader(user) Long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        log.info("POST (user: {}) /requests/all?from={}&size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

}
