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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
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
        ItemDtoPost itemDtoPostWrong = new ItemDtoPost();
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoPostWrong, 1L));
        itemDtoPostWrong.setAvailable(true);
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoPostWrong, 1L));
        itemDtoPostWrong.setName("Wrong");
        assertThrows(BadRequest.class, () -> service.saveItem(itemDtoPostWrong, 1L));
        itemDtoPostWrong.setDescription("Wrong");
        assertThrows(NotFoundException.class, () -> service.saveItem(itemDtoPostWrong, 1L));
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        ItemDtoPost itemDtoPost = makeItemDtoPost("Item", "Item", true);
        service.saveItem(itemDtoPost, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item = query.setParameter("description", itemDtoPost.getDescription())
                .getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getDescription(), equalTo(itemDtoPost.getDescription()));
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
        ItemDtoPost itemDtoChanged = makeItemDtoPost("NewItem", "NewItem", true);
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
        User user2 = makeUser("user2", "user2@mail.com");
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
        Booking booking1 = makeBooking(LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                ItemMapper.fromItemDto(item1, user),
                user,
                Status.WAITING);
        Booking booking2 = makeBooking(LocalDateTime.now().minusDays(2),
                LocalDateTime.now(),
                ItemMapper.fromItemDto(item1, user),
                user,
                Status.WAITING);
        em.persist(booking1);
        em.persist(booking2);
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
        Comment comment = makeComment("Comment", user, item);
        em.persist(comment);
        ItemDto itemFromDb = service.getItemByItemAndUserId(item.getId(), user.getId());
        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @Order(5)
    void searchByItemName() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        User user2 = makeUser("user2", "user2@mail.com");
        em.persist(user2);
        ItemDto itemDto1 = makeItemDto("Item1", "Item1", true);
        ItemDto itemDto2 = makeItemDto("Item2", "Item2", true);
        ItemDto itemDto3 = makeItemDto("Item3", "Item3", true);
        Item item1 = ItemMapper.fromItemDto(itemDto1, user);
        Item item2 = ItemMapper.fromItemDto(itemDto2, user);
        Item item3 = ItemMapper.fromItemDto(itemDto3, user);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();
        assertThat(service.searchByItemName(" ", user.getId(), 0, 10), hasSize(0));
        List<ItemDto> itemFromDb = service.searchByItemName("2", user.getId(), 0, 10);
        assertThat(itemFromDb, hasSize(1));
        for (ItemDto itemDto : itemFromDb) {
            assertThat(itemFromDb, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
        BookingDto bookingDto = makeBookingDto(user2.getId(), item1.getId(),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(4));
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user2, item1);
        em.persist(booking);
    }

    @Test
    @Order(6)
    void postComment() {
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        ItemDto item = makeItemDto("itemName", "itemDescription", true);
        em.persist(ItemMapper.fromItemDto(item, user));
        Long itemId = em.createQuery("Select id From Item i", Long.class).getSingleResult();
        user.setId(user.getId());
        item.setId(itemId);
        CommentDtoPost commentDtoWrong = new CommentDtoPost();
        assertThrows(BadRequest.class, () -> service.postComment(user.getId(), itemId, commentDtoWrong));
        BookingDto bookingDto = makeBookingDto(user.getId(), itemId, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4));
        bookingDto.setStatus(Status.APPROVED);
        em.persist(BookingMapper.fromBookingDto(bookingDto, user, ItemMapper.fromItemDto(item, user)));
        assertThrows(BadRequest.class, () -> service.postComment(user.getId(), itemId, commentDtoWrong));
        commentDtoWrong.setText("  ");
        assertThrows(BadRequest.class, () -> service.postComment(user.getId(), itemId, commentDtoWrong));
        commentDtoWrong.setText("Text");
        assertThrows(BadRequest.class, () -> service.postComment(user.getId(), itemId, commentDtoWrong));
        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        em.persist(BookingMapper.fromBookingDto(bookingDto, user, ItemMapper.fromItemDto(item, user)));
        CommentDtoPost comment = makeCommentDtoPost("commentText");
        service.postComment(user.getId(), itemId, comment);
        Comment commentFromDb = em.createQuery("Select c From Comment c", Comment.class).getSingleResult();
        assertThat(commentFromDb.getId(), notNullValue());
        assertThat(commentFromDb.getText(), equalTo(comment.getText()));
    }

    private CommentDtoPost makeCommentDtoPost(String commentText) {
        return new CommentDtoPost(commentText);
    }

    private ItemDtoPost makeItemDtoPost(String name, String description, boolean available) {
        ItemDtoPost itemDtoPost = new ItemDtoPost();
        itemDtoPost.setAvailable(available);
        itemDtoPost.setName(name);
        itemDtoPost.setDescription(description);
        return itemDtoPost;
    }

    private BookingDto makeBookingDto(Long userId, Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
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

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User user, Status status) {
        Booking b = new Booking();
        b.setStart(start);
        b.setEnd(end);
        b.setItem(item);
        b.setBooker(user);
        b.setStatus(status);
        return b;
    }

}