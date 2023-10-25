package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserController;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public ItemDto post(@RequestBody @Valid ItemDto item, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST /items");
        return itemService.post(item, userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(AdvancedInfo.class)
    public ItemDto patch(@RequestBody @Valid ItemDto item, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemService.patch(item, itemId, userId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /items");
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /items");
        return itemService.getItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String itemName) {
        log.info("GET /items");
        return itemService.search(itemName);
    }
}
