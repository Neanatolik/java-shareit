package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@Positive @RequestHeader(user) Long userId,
                                                  @RequestBody @Valid ItemRequestDtoPost itemRequestDtoPost) {
        log.info("POST (user: {}) /requests", userId);
        return itemRequestClient.saveItemRequest(itemRequestDtoPost, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequest(@Positive @RequestHeader(user) Long userId) {
        log.info("GET (user: {}) /requests", userId);
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@Positive @RequestHeader(user) Long userId,
                                                     @Positive @PathVariable Long itemRequestId) {
        log.info("GET (user: {}) /requests/{}", userId, itemRequestId);
        return itemRequestClient.getItemRequestsByOwnerById(userId, itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsPage(@Positive @RequestHeader(user) Long userId,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("POST (user: {}) /requests/all?from={}&size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

}
