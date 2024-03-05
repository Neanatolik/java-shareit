package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService service;

    @Test
    @Order(1)
    void saveItem() {
        ItemDto itemDtoWrong = new ItemDto();
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoWrong, 1L));
        itemDtoWrong.setAvailable(true);
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoWrong, 1L));
        itemDtoWrong.setName("Wrong");
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoWrong, 1L));
        itemDtoWrong.setDescription("Wrong");
        assertThrows(NotFoundException.class, () -> service.saveItem(itemDtoWrong, 1L));
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        ItemDto itemDto = makeItemDto("Item", "Item", true);
        service.saveItem(itemDto, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item = query.setParameter("description", itemDto.getDescription())
                .getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @Order(2)
    void changeItem() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        User user2 = makeUser("user2", "user2@mail.com");
        em.persist(user2);
        ItemDto itemDto = makeItemDto("Item", "Item", true);
        Item item1 = ItemMapper.fromItemDto(itemDto, user);
        em.persist(item1);
        ItemDto itemDtoChanged = makeItemDto("NewItem", "NewItem", true);
        assertThrows(NotFoundException.class, () -> service.changeItem(itemDtoChanged, item1.getId(), 3L));
        service.changeItem(itemDtoChanged, item1.getId(), user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item = query.setParameter("description", itemDtoChanged.getDescription())
                .getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getDescription(), equalTo(itemDtoChanged.getDescription()));
    }

    @Test
    @Order(3)
    void getItemsByUserId() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        List<ItemDto> itemDtos = new java.util.ArrayList<>(List.of(
                makeItemDto("Item1", "Item1", true),
                makeItemDto("Item2", "Item2", true),
                makeItemDto("Item3", "Item3", false)
        ));
        for (ItemDto itemDto : itemDtos) {
            Item entity = ItemMapper.fromItemDto(itemDto, user);
            em.persist(entity);
        }
        em.flush();
        List<ItemDto> targetItems = service.getItemsByUserId(user.getId());
        assertThat(targetItems, hasSize(itemDtos.size()));
        for (ItemDto itemDto : itemDtos) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
        ItemDto item1 = makeItemDto("ItemForComment", "WithComment", true);
        em.persist(ItemMapper.fromItemDto(item1, user));
        Long itemId = em.createQuery("Select id From Item i Where description = 'WithComment'", Long.class).getSingleResult();
        item1.setId(itemId);
        Comment comment = makeComment("TextComment", user, ItemMapper.fromItemDto(item1, user));
        em.persist(comment);
        targetItems = service.getItemsByUserId(user.getId());
        itemDtos.add(item1);
        assertThat(targetItems, hasSize(itemDtos.size()));
        for (ItemDto itemDto : itemDtos) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
    }

    @Test
    @Order(4)
    void getItemByItemAndUserId() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        ItemDto itemDto = makeItemDto("Item", "Item", true);
        Item item = ItemMapper.fromItemDto(itemDto, user);
        em.persist(item);
        ItemDto itemFromDb = service.getItemByItemAndUserId(item.getId(), user.getId());
        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @Order(5)
    void searchByItemName() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        List<ItemDto> itemDtos = List.of(
                makeItemDto("Item1", "Item1", true),
                makeItemDto("Item2", "Item2", true),
                makeItemDto("Item3", "Item3", false)
        );
        for (ItemDto itemDto : itemDtos) {
            Item entity = ItemMapper.fromItemDto(itemDto, user);
            em.persist(entity);
        }
        em.flush();
        assertThat(service.searchByItemName(" ", user.getId()), hasSize(0));
        List<ItemDto> itemFromDb = service.searchByItemName("2", user.getId());
        assertThat(itemFromDb, hasSize(1));
        for (ItemDto itemDto : itemFromDb) {
            assertThat(itemFromDb, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
    }

    @Test
    @Order(6)
    void postComment() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        ItemDto item = makeItemDto("itemName", "itemDescription", true);
        em.persist(ItemMapper.fromItemDto(item, user));
        Long userId = em.createQuery("Select id From User u", Long.class).getSingleResult();
        Long itemId = em.createQuery("Select id From Item i", Long.class).getSingleResult();
        user.setId(userId);
        item.setId(itemId);
        Comment commentDtoWrong = new Comment();
        assertThrows(BadRequest.class, () -> service.postComment(userId, itemId, commentDtoWrong));
        BookingDto bookingDto = makeBooking(userId, itemId);
        bookingDto.setStatus(Status.APPROVED);
        em.persist(BookingMapper.fromBookingDto(bookingDto, user, ItemMapper.fromItemDto(item, user)));
        assertThrows(BadRequest.class, () -> service.postComment(userId, itemId, commentDtoWrong));
        commentDtoWrong.setText("  ");
        assertThrows(BadRequest.class, () -> service.postComment(userId, itemId, commentDtoWrong));
        commentDtoWrong.setText("Text");
        assertThrows(BadRequest.class, () -> service.postComment(userId, itemId, commentDtoWrong));
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        em.persist(BookingMapper.fromBookingDto(bookingDto, user, ItemMapper.fromItemDto(item, user)));
        Comment comment = makeComment("commentText", user, ItemMapper.fromItemDto(item, user));
        service.postComment(userId, itemId, comment);
        Comment commentFromDb = em.createQuery("Select c From Comment c", Comment.class).getSingleResult();
        assertThat(commentFromDb.getId(), notNullValue());
        assertThat(commentFromDb.getText(), equalTo(comment.getText()));

    }

    private BookingDto makeBooking(Long userId, Long itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingDto.setItemId(itemId);
        bookingDto.setBooker(userId);
        return bookingDto;
    }

    private ItemDto makeItemDto(String name, String description, boolean isAvailable) {
        ItemDto item = new ItemDto();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(isAvailable);
        return item;
    }

    private User makeUser(String name, String email) {
        User dto = new User();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private Comment makeComment(String text, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

}