package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private long nextId = 0L;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public ItemDto post(ItemDto itemDto, long userId) {
        checkItem(itemDto);
        checkItemsUser(userId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.fromItemDto(itemDto, getNextId(), userId)),
                getLastBooking(ItemMapper.fromItemDto(itemDto, nextId, userId), userId),
                getNextBooking(ItemMapper.fromItemDto(itemDto, nextId, userId), userId),
                getComments(nextId));
    }

    @Override
    @Transactional
    public ItemDto patch(ItemDto itemDto, long itemId, long userId) {
        checkItemsUser(userId);
        checkBelong(itemId, userId);
        Item item1 = itemRepository.save(updateItem(ItemMapper.fromItemDto(itemDto, itemId, userId), itemRepository.getReferenceById(itemId)));
        return ItemMapper.toItemDto(item1,
                getLastBooking(item1, userId),
                getNextBooking(item1, userId),
                getComments(itemId));

    }

    @Override
    public List<ItemDto> getItems(long userId) {
        checkItemsUser(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.getItems(userId)) {
            itemsDto.add(ItemMapper.toItemDto(item,
                    getLastBooking(item, userId),
                    getNextBooking(item, userId),
                    getComments(item.getId())));
        }
        return itemsDto;
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {
        Item item = getItemById(itemId);
        return ItemMapper.toItemDto(item,
                getLastBooking(item, userId),
                getNextBooking(item, userId),
                getComments(itemId));
    }

    @Override
    public List<ItemDto> search(String itemName, long userId) {
        if (itemName.isBlank()) return Collections.emptyList();
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.search(itemName)) {
            itemsDto.add(ItemMapper.toItemDto(item,
                    getLastBooking(item, userId),
                    getNextBooking(item, userId),
                    getComments(item.getId())));
        }
        return itemsDto;
    }

    @Override
    @Transactional
    public CommentDto postComment(long userId, Long itemId, Comment comment) {
        checkItemsUser(userId);
        checkAvailabilityOfBookingForUser(userId, itemId);
        checkText(comment.getText());
        checkDateOfBookingForComment(userId, itemId);
        comment.setItem(itemId);
        comment.setAuthor(userId);
        String authorName = userRepository.getReferenceById(userId).getName();
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment), authorName);
    }

    private List<CommentDto> getComments(long itemId) {
        List<Comment> comments = Collections.emptyList();
        if (commentRepository.getItemIds().contains(itemId)) {
            comments = commentRepository.getCommentsByItemId(itemId);
        }
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toCommentDto(comment, userRepository.getReferenceById(comment.getAuthor()).getName()));
        }
        return commentsDto;
    }

    private void checkDateOfBookingForComment(long userId, Long itemId) {
        if (bookingRepository.getBookingsByStatusAndUserAndItemAndTime(userId, itemId).isEmpty()) {
            throw new BadRequest("Ещё не время!");
        }
    }

    private void checkText(String text) {
        if (Objects.isNull(text)) {
            throw new BadRequest("Текст не предоставлен");
        }
        if (text.isBlank() || text.isEmpty()) {
            throw new BadRequest("Текст пустой");
        }
    }

    private void checkAvailabilityOfBookingForUser(long userId, Long itemId) {
        if (!(bookingRepository.getBookerByItemId(itemId).contains(userId))) {
            throw new BadRequest("У данного пользователя нет бронирования");
        }
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Item %d doesn't exist", id)));
    }

    private void checkBelong(long itemId, long userId) {
        Long itemFromId = itemRepository.getReferenceById(itemId).getOwner();
        if (!Objects.equals(itemFromId, userId)) {
            throw new NotFoundException(String.format("User %d doesn't have item %d", userId, itemId));
        }
    }

    private Item updateItem(Item itemNew, Item itemOld) {
        if (Objects.nonNull(itemNew.getAvailable())) itemOld.setAvailable(itemNew.getAvailable());
        if (Objects.nonNull(itemNew.getName())) itemOld.setName(itemNew.getName());
        if (Objects.nonNull(itemNew.getDescription())) itemOld.setDescription(itemNew.getDescription());
        return itemOld;
    }

    private BookingDtoForItem getLastBooking(Item item, long userId) {
        BookingDtoForItem bookingDtoForItem = null;
        if (!item.getAvailable()) {
            return null;
        }
        List<Booking> bookings = bookingRepository.getBookingsByItemId(item.getId(), userId);
        if (bookings.isEmpty()) {
            return null;
        }
        if (bookings.get(0).getStart().isAfter(LocalDateTime.now())) {
            return null;
        }
        for (Booking booking : bookings) {
            if (!booking.getStart().isAfter(LocalDateTime.now())) {
                bookingDtoForItem = BookingMapper.toBookingDtoForItem(booking);
            }
        }
        return bookingDtoForItem;
    }

    private BookingDtoForItem getNextBooking(Item item, long userId) {
        BookingDtoForItem bookingDtoForItem = null;
        if (!item.getAvailable()) {
            return null;
        }
        List<Booking> bookings = bookingRepository.getBookingsByItemIdDesc(item.getId(), userId);
        if (bookings.isEmpty()) {
            return null;
        }
        if (bookings.get(0).getEnd().isBefore(LocalDateTime.now())) {
            return null;
        }
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                bookingDtoForItem = BookingMapper.toBookingDtoForItem(booking);
            }
        }
        return bookingDtoForItem;
    }


    private void checkItemsUser(long userId) {
        if (!userRepository.getIds().contains(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

    private void checkItem(ItemDto item) {
        if (Objects.isNull(item.getAvailable())) {
            throw new BadRequest("Item without available");
        } else if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            throw new BadRequest("Item without name");
        } else if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            throw new BadRequest("Item without description");
        }
    }

    private long getNextId() {
        return ++nextId;
    }

}
