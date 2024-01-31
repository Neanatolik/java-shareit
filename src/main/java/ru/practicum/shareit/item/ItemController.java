package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final String user = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public ItemDto saveItem(@RequestBody @Valid ItemDto item, @RequestHeader(user) long userId) {
        log.info("POST /items");
        return itemService.saveItem(item, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(user) long userId) {
        log.info("GET (user: {}) /items", userId);
        return itemService.getItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(AdvancedInfo.class)
    public ItemDto changeItem(@RequestBody @Valid ItemDto item, @PathVariable long itemId, @RequestHeader(user) long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemService.changeItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemByItemAndUserId(@PathVariable Long itemId, @RequestHeader(user) long userId) {
        log.info("GET /items/{}", itemId);
        return itemService.getItemByItemAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByItemName(@RequestParam("text") String itemName, @RequestHeader(user) long userId) {
        log.info("GET /items");
        return itemService.searchByItemName(itemName, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(user) long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody @Valid Comment comment) {
        log.info("POST /{}/comment", itemId);
        return itemService.postComment(userId, itemId, comment);
    }
}
