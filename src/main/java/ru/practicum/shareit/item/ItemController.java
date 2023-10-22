package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserController;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto post(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items");
        return itemService.post(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestBody Item item, @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemService.patch(item, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items");
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items");
        return itemService.getItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String itemName) {
        log.info("GET /items");
        return itemService.search(itemName);
    }
}
