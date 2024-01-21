package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private long nextId = 0L;

    @Autowired
    public BookingServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDtoSend post(BookingDto bookingDto, long userId) {
        checkUser(userId);
        checkItemId(bookingDto.getItemId());
        checkIsUserOwnerItem(userId, bookingDto.getItemId());
        checkItemAvailable(bookingDto.getItemId());
        checkDate(bookingDto.getStart(), bookingDto.getEnd());
        bookingDto.setBooker(userId);
        bookingDto.setStatus(Status.WAITING);
        return BookingMapper.toBookingDtoSend(
                bookingRepository.save(BookingMapper.fromBookingDto(bookingDto, getNextId())),
                ItemMapper.toItemDtoForBooking(itemRepository.getReferenceById(bookingDto.getItemId())),
                UserMapper.toUserDto(userRepository.getReferenceById(userId)));
    }

    @Override
    public BookingDtoSend patch(long bookingId, boolean approved, long userId) {
        checkBookingId(bookingId);
        checkStatus(bookingId);
        checkOwning(bookingId, userId);
        Booking booking = getBookingById(bookingId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoSend(
                bookingRepository.save(booking),
                ItemMapper.toItemDtoForBooking(itemRepository.getReferenceById(booking.getItemId())),
                UserMapper.toUserDto(userRepository.getReferenceById(booking.getBooker())));
    }

    @Override
    public BookingDtoSend get(long bookingId, long userId) {
        checkBookingId(bookingId);
        checkUser(userId);
        checkBookingUser(bookingId, userId);
        Booking booking = getBookingById(bookingId);
        return BookingMapper.toBookingDtoSend(
                booking,
                ItemMapper.toItemDtoForBooking(itemRepository.getReferenceById(booking.getItemId())),
                UserMapper.toUserDto(userRepository.getReferenceById(booking.getBooker())));
    }

    @Override
    public List<BookingDtoSend> get(long userId, String state) {
        checkUser(userId);
        System.out.println();
        checkState(state);
        System.out.println("bookings: " + bookingRepository.getBookingsItemAll(userId));
        List<Booking> listBooking = getListOfBookingByStateAndUser(userId, state);
        System.out.println("listBooking: " + listBooking);
        List<BookingDtoSend> listBookingDtoSend = new LinkedList<>();
        for (Booking b : listBooking) {
            listBookingDtoSend.add(BookingMapper.toBookingDtoSend(b,
                    ItemMapper.toItemDtoForBooking(itemRepository.getReferenceById(b.getItemId())),
                    UserMapper.toUserDto(userRepository.getReferenceById(b.getBooker()))));
        }
        return listBookingDtoSend;
    }

    @Override
    public List<BookingDtoSend> getOwnersItem(long ownerId, String state) {
        checkUser(ownerId);
        checkState(state);
        List<Booking> listBooking = getListOfBookingByStateAndUserForOwner(ownerId, state);
        List<BookingDtoSend> listBookingDtoSend = new LinkedList<>();
        for (Booking b : listBooking) {
            listBookingDtoSend.add(BookingMapper.toBookingDtoSend(b,
                    ItemMapper.toItemDtoForBooking(itemRepository.getReferenceById(b.getItemId())),
                    UserMapper.toUserDto(userRepository.getReferenceById(b.getBooker()))));
        }
        return listBookingDtoSend;
    }

    private void checkOwning(long bookingId, long userId) {
        if (!(bookingRepository.getOwnersFromBookingByBookingId(bookingId) == userId)) {
            throw new NotFoundException("Не тот пользователь");
        }
    }

    private void checkStatus(Long bookingId) {
        if (bookingRepository.getStatusByBookingId(bookingId).equals(Status.APPROVED)) {
            throw new BadRequest("Уже APPROVED");
        }
    }

    private List<Booking> getListOfBookingByStateAndUserForOwner(long userId, String state) {
        List<Booking> listBooking;
        switch (Status.valueOf(state)) {
            case ALL:
                listBooking = bookingRepository.getOwnersItemAll(userId);
                break;
            case PAST:
                listBooking = bookingRepository.getOwnersItemPast(userId);
                break;
            case FUTURE:
                listBooking = bookingRepository.getOwnersItemFuture(userId);
                break;
            case CURRENT:
                listBooking = bookingRepository.getOwnersItemCurrent(userId);
                break;
            case WAITING:
            case APPROVED:
            case CANCELED:
            case REJECTED:
                listBooking = bookingRepository.getBookingsByStatusAndOwnerId(userId, state);
                break;
            default:
                listBooking = Collections.emptyList();
        }
        return listBooking;
    }

    private List<Booking> getListOfBookingByStateAndUser(long userId, String state) {
        List<Booking> listBooking;
        switch (Status.valueOf(state)) {
            case ALL:
                listBooking = bookingRepository.getBookingsItemAll(userId);
                break;
            case PAST:
                listBooking = bookingRepository.getBookingsItemPast(userId);
                break;
            case FUTURE:
                listBooking = bookingRepository.getBookingsItemFuture(userId);
                break;
            case CURRENT:
                listBooking = bookingRepository.getBookingsCurrentByUserId(userId);
                break;
            case WAITING:
            case APPROVED:
            case CANCELED:
            case REJECTED:
                listBooking = bookingRepository.getBookingsByStatusAndUserId(userId, state);
                break;
            default:
                listBooking = Collections.emptyList();
                break;
        }
        return listBooking;
    }

    private void checkBookingUser(long bookingId, long userId) {
        if (!(bookingRepository.getBookersFromBookingByBookingId(bookingId) == userId ||
                bookingRepository.getOwnersFromBookingByBookingId(bookingId) == userId)) {
            throw new NotFoundException("Недоступно!");
        }

    }

    private void checkState(String state) {
        List<String> status = Stream.of(Status.values()).map(Status::name).collect(Collectors.toList());
        System.out.println(state);
        if (!status.contains(state)) {
            throw new BadRequest("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkBookingId(Long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NotFoundException(String.format("Бронирования: %d не существует", bookingId));
        }
    }

    private void checkDate(LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(start)) {
            throw new BadRequest("Отсутствует дата старта!");
        }
        if (Objects.isNull(end)) {
            throw new BadRequest("Отсутствует дата окончания!");
        }
        if (!start.isBefore(end)) {
            throw new BadRequest("Дата окончания предшествует дате начала!");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequest("Дата старта в прошлом!");
        }
        if (end.isBefore(LocalDateTime.now())) {
            throw new BadRequest("Дата окончания в прошлом!");
        }
        for (Booking booking : bookingRepository.getBookingsByStatus(Status.APPROVED)) {
            if (!(end.isBefore(booking.getStart()) || start.isBefore(booking.getEnd()))) {
                throw new BadRequest("Занято");
            }
        }

    }

    private void checkItemAvailable(Long itemId) {
        if (!getItemById(itemId).getAvailable()) {
            throw new BadRequest(String.format("Item: %d is not available", itemId));
        }
    }

    private Item getItemById(Long id) {
        Optional<Item> optItem = itemRepository.findById(id);
        if (optItem.isEmpty()) {
            throw new NotFoundException(String.format("Item %d doesn't exist", id));
        }
        return optItem.get();
    }

    private Booking getBookingById(Long id) {
        Optional<Booking> optItem = bookingRepository.findById(id);
        if (optItem.isEmpty()) {
            throw new NotFoundException(String.format("Booking %d doesn't exist", id));
        }
        return optItem.get();
    }

    private void checkUser(long userId) {
        if (!userRepository.getIds().contains(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

    private void checkItemId(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Такого item не существует");
        }
    }

    private void checkIsUserOwnerItem(long userId, Long itemId) {
        if (itemRepository.getReferenceById(itemId).getOwner() == userId) {
            throw new NotFoundException("Пользователь явлется владельцем вещи");
        }
    }


    private long getNextId() {
        return ++nextId;
    }

}