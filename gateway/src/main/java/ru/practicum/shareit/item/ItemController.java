package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemDtoPost itemDtoPost, @RequestHeader(user) long userId) {
        log.info("POST /items");
        return itemClient.saveItem(itemDtoPost, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(user) long userId) {
        log.info("GET (user: {}) /items", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(AdvancedInfo.class)
    public ResponseEntity<Object> changeItem(@RequestBody @Valid ItemDtoPost itemDtoPost,
                                             @PathVariable long itemId,
                                             @RequestHeader(user) long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemClient.changeItem(itemDtoPost, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByItemAndUserId(@PathVariable Long itemId, @RequestHeader(user) long userId) {
        log.info("GET /items/{}", itemId);
        return itemClient.getItemByItemAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByItemName(@RequestParam("text") String text,
                                                   @RequestHeader(user) long userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
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
