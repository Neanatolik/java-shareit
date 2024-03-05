package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public BookingDtoSend saveBooking(BookingDto bookingDto, long userId) {
        checkUser(userId);
        checkItemId(bookingDto.getItemId());
        checkIsUserOwnerItem(userId, bookingDto.getItemId());
        checkItemAvailable(bookingDto.getItemId());
        checkDate(bookingDto.getStart(), bookingDto.getEnd());
        bookingDto.setStatus(Status.WAITING);
        User user = userRepository.getReferenceById(userId);
        Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        return BookingMapper.toBookingDtoSend(bookingRepository.save(BookingMapper.fromBookingDto(bookingDto, user, item)));
    }

    @Override
    @Transactional
    public BookingDtoSend changeBooking(long bookingId, boolean approved, long userId) {
        checkBookingId(bookingId);
        checkStatus(bookingId);
        checkOwning(bookingId, userId);
        Booking booking = getBookingById(bookingId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoSend(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoSend getBookingWithoutState(long bookingId, long userId) {
        checkBookingId(bookingId);
        checkUser(userId);
        checkBookingUser(bookingId, userId);
        Booking booking = getBookingById(bookingId);
        return BookingMapper.toBookingDtoSend(booking);
    }

    @Override
    public List<BookingDtoSend> getBookingWithState(long userId, String state, int from, int size) {
        checkUser(userId);
        checkState(state);
        checkFromAndSize(from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> listBooking = getListOfBookingByStateAndUser(userId, state, page);
        List<BookingDtoSend> listBookingDtoSend = new LinkedList<>();
        for (Booking b : listBooking) {
            listBookingDtoSend.add(BookingMapper.toBookingDtoSend(b));
        }
        return listBookingDtoSend;
    }

    @Override
    public List<BookingDtoSend> getOwnersItem(long ownerId, String state, int from, int size) {
        checkUser(ownerId);
        checkState(state);
        checkFromAndSize(from, size);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> listBooking = getListOfBookingByStateAndUserForOwner(ownerId, state, page);
        List<BookingDtoSend> listBookingDtoSend = new LinkedList<>();
        for (Booking b : listBooking) {
            listBookingDtoSend.add(BookingMapper.toBookingDtoSend(b));
        }
        return listBookingDtoSend;
    }

    private void checkFromAndSize(int from, int size) {
        if ((from < 0) || (size <= 0)) {
            throw new BadRequest("Неверные значения");
        }
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

    private List<Booking> getListOfBookingByStateAndUserForOwner(long userId, String state, PageRequest page) {
        List<Booking> listBooking;
        switch (Status.valueOf(state)) {
            case ALL:
                listBooking = bookingRepository.getOwnersItemAll(userId, page);
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

    private List<Booking> getListOfBookingByStateAndUser(long userId, String state, PageRequest page) {
        List<Booking> listBooking;
        switch (Status.valueOf(state)) {
            case ALL:
                listBooking = bookingRepository.getBookingsItemAll(userId, page);
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

    }

    private void checkItemAvailable(Long itemId) {
        if (!getItemById(itemId).getAvailable()) {
            throw new BadRequest(String.format("Item: %d is not available", itemId));
        }
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Item %d doesn't exist", id)));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking %d doesn't exist", id)));
    }

    private void checkUser(long userId) {
        if (!userRepository.existUserId(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

    private void checkItemId(Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такого item не существует"));
    }

    private void checkIsUserOwnerItem(long userId, Long itemId) {
        if (itemRepository.getReferenceById(itemId).getOwner().getId() == userId) {
            throw new NotFoundException("Пользователь явлется владельцем вещи");
        }
    }
}
