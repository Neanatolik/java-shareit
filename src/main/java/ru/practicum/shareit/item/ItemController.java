package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
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
    public ItemDto saveItem(@RequestBody @Valid ItemDtoPost itemDtoPost, @RequestHeader(user) long userId) {
        log.info("POST /items");
        return itemService.saveItem(itemDtoPost, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(user) long userId) {
        log.info("GET (user: {}) /items", userId);
        return itemService.getItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(AdvancedInfo.class)
    public ItemDto changeItem(@RequestBody @Valid ItemDtoPost itemDtoPost,
                              @PathVariable long itemId,
                              @RequestHeader(user) long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemService.changeItem(itemDtoPost, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemByItemAndUserId(@PathVariable Long itemId, @RequestHeader(user) long userId) {
        log.info("GET /items/{}", itemId);
        return itemService.getItemByItemAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByItemName(@RequestParam("text") String itemName,
                                          @RequestHeader(user) long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items");
        return itemService.searchByItemName(itemName, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(user) long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody @Valid CommentDtoPost comment) {
        log.info("POST /{}/comment", itemId);
        return itemService.postComment(userId, itemId, comment);
    }
}
