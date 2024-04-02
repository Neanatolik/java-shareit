package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemDtoPost itemDtoPost, @Positive @RequestHeader(user) long userId) {
        log.info("POST /items");
        return itemClient.saveItem(itemDtoPost, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@Positive @RequestHeader(user) long userId) {
        log.info("GET (user: {}) /items", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@RequestBody ItemDtoPost itemDtoPost, @Positive @PathVariable long itemId, @Positive @RequestHeader(user) long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemClient.changeItem(itemDtoPost, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByItemAndUserId(@Positive @PathVariable Long itemId, @Positive @RequestHeader(user) long userId) {
        log.info("GET /items/{}", itemId);
        return itemClient.getItemByItemAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByItemName(@RequestParam("text") String text, @Positive @RequestHeader(user) long userId, @PositiveOrZero @RequestParam(defaultValue = "0") int from, @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/search?text={}", text);
        return itemClient.searchByItemName(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(user) long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid CommentDtoPost comment) {
        log.info("POST /{}/comment", itemId);
        return itemClient.postComment(userId, itemId, comment);
    }
}
