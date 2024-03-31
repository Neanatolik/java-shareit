package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService service;

    @Test
    @Order(1)
    void saveBooking() {
        User user = makeUser("User1", "User1@mail.ru");
        em.persist(user);
        User user2 = makeUser("User2", "User2@mail.ru");
        em.persist(user2);
        Item itemWrong = makeItem("ItemWrong", "ItemWrongDescription", user, false);
        em.persist(itemWrong);
        Item item = makeItem("Item1", "Item1Description", user, true);
        em.persist(item);
        BookingDto bookingDtoWrong = makeBookingDto(0L, 0L, null, null, null);
        assertThrows(NotFoundException.class, () -> service.saveBooking(bookingDtoWrong, 0));
        assertThrows(NotFoundException.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setBooker(user.getId());
        assertThrows(NotFoundException.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setItemId(itemWrong.getId());
        assertThrows(NotFoundException.class, () -> service.saveBooking(bookingDtoWrong, user.getId()));
        assertThrows(BadRequest.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setItemId(item.getId());
        assertThrows(BadRequest.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setStart(LocalDateTime.now().minusDays(2));
        assertThrows(BadRequest.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setEnd(LocalDateTime.now().minusDays(3));
        assertThrows(BadRequest.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        bookingDtoWrong.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(BadRequest.class, () -> service.saveBooking(bookingDtoWrong, user2.getId()));
        BookingDto bookingDto = makeBookingDto(item.getId(), user.getId(), Status.WAITING,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        service.saveBooking(bookingDto, user2.getId());

    }

    @Test
    @Order(2)
    void changeBooking() {
        User user = makeUser("User1", "User1@mail.ru");
        em.persist(user);
        User user2 = makeUser("User2", "User2@mail.ru");
        em.persist(user2);
        Item item = makeItem("Item1", "Item1Description", user, true);
        em.persist(item);
        BookingDto bookingDto = makeBookingDto(item.getId(), user.getId(), Status.WAITING,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user2, item);
        em.persist(booking);
        service.changeBooking(booking.getId(), false, user.getId());
        service.changeBooking(booking.getId(), true, user.getId());
        assertThrows(BadRequest.class, () -> service.changeBooking(booking.getId(), true, user.getId()));
        assertThrows(NotFoundException.class, () -> service.changeBooking(0, true, user.getId()));
    }

    @Test
    @Order(3)
    void getBookingWithoutState() {
        User user = makeUser("User1", "User1@mail.ru");
        em.persist(user);
        User user2 = makeUser("User2", "User2@mail.ru");
        em.persist(user2);
        Item item = makeItem("Item1", "Item1Description", user, true);
        em.persist(item);
        BookingDto bookingDto = makeBookingDto(item.getId(), user.getId(), Status.WAITING,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user2, item);
        em.persist(booking);
        service.getBookingWithoutState(booking.getId(), user.getId());

    }

    @Test
    @Order(4)
    void getBookingWithState() {
        User user = makeUser("User1", "User1@mail.ru");
        em.persist(user);
        User user2 = makeUser("User2", "User2@mail.ru");
        em.persist(user2);
        Item item = makeItem("Item1", "Item1Description", user, true);
        em.persist(item);
        BookingDto bookingDto = makeBookingDto(item.getId(), user.getId(), Status.WAITING,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user2, item);
        em.persist(booking);
        service.getBookingWithState(user2.getId(), Status.ALL.toString(), 0, 10);
        service.getBookingWithState(user2.getId(), Status.PAST.toString(), 0, 10);
        service.getBookingWithState(user2.getId(), Status.FUTURE.toString(), 0, 10);
        service.getBookingWithState(user2.getId(), Status.CURRENT.toString(), 0, 10);
        service.getBookingWithState(user2.getId(), Status.WAITING.toString(), 0, 10);
    }

    @Test
    @Order(5)
    void getOwnersItem() {
        User user = makeUser("User1", "User1@mail.ru");
        em.persist(user);
        User user2 = makeUser("User2", "User2@mail.ru");
        em.persist(user2);
        Item item = makeItem("Item1", "Item1Description", user, true);
        em.persist(item);
        BookingDto bookingDto = makeBookingDto(item.getId(), user.getId(), Status.WAITING,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = BookingMapper.fromBookingDto(bookingDto, user2, item);
        em.persist(booking);
        service.getOwnersItem(user.getId(), Status.ALL.toString(), 0, 10);
        service.getOwnersItem(user.getId(), Status.PAST.toString(), 0, 10);
        service.getOwnersItem(user.getId(), Status.FUTURE.toString(), 0, 10);
        service.getOwnersItem(user.getId(), Status.CURRENT.toString(), 0, 10);
        service.getOwnersItem(user.getId(), Status.WAITING.toString(), 0, 10);
    }

    private BookingDto makeBookingDto(Long itemId, Long userId, Status status, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        bookingDto.setBooker(userId);
        bookingDto.setStatus(status);
        return bookingDto;
    }

    private User makeUser(String name, String email) {
        User dto = new User();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private Item makeItem(String name, String description, User user, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setOwner(user);
        item.setAvailable(available);
        item.setDescription(description);
        return item;
    }
}