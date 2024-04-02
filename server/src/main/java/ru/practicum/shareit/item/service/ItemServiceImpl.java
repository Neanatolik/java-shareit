package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

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
    public ItemDto saveItem(ItemDtoPost itemDtoPost, long userId) {
        checkItemsUser(userId);
        User user = userRepository.getReferenceById(userId);
        Item item = itemRepository.save(ItemMapper.fromItemDtoPost(itemDtoPost, user));
        return ItemMapper.toItemDto(item,
                getLastBooking(item, userId),
                getNextBooking(item, userId),
                getCommentsByItemId(item.getId()));
    }

    @Override
    @Transactional
    public ItemDto changeItem(ItemDtoPost itemDtoPost, long itemId, long userId) {
        checkItemsUser(userId);
        checkBelong(itemId, userId);
        User user = userRepository.getReferenceById(userId);
        Item item = itemRepository.save(updateItem(ItemMapper.fromItemDtoPost(itemDtoPost, user),
                itemRepository.getReferenceById(itemId)));
        return ItemMapper.toItemDto(item,
                getLastBooking(item, userId),
                getNextBooking(item, userId),
                getCommentsByItemId(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        checkItemsUser(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Booking> bookings = bookingRepository.getBookingsByUserId(userId);
        List<Item> items = itemRepository.getItems(userId);
        List<Comment> comments = commentRepository.findAll();
        for (Item item : items) {
            List<Booking> bookingsByItem = bookings.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .collect(Collectors.toList());
            itemsDto.add(ItemMapper.toItemDto(item,
                    getLastBookingWithoutCycle(item, bookingsByItem),
                    getNextBookingWithoutCycle(item, bookingsByItem),
                    getCommentsByItemIdWithoutCycle(comments, item.getId())));
        }
        return itemsDto;
    }

    @Override
    public ItemDto getItemByItemAndUserId(long itemId, long userId) {
        Item item = getItemById(itemId);
        return ItemMapper.toItemDto(item,
                getLastBooking(item, userId),
                getNextBooking(item, userId),
                getCommentsByItemId(itemId));
    }

    @Override
    public List<ItemDto> searchByItemName(String itemName, long userId, int from, int size) {
        if (itemName.isBlank()) return Collections.emptyList();
        List<ItemDto> itemsDto = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Booking> bookings = bookingRepository.getBookingsByUserId(userId);
        List<Item> items = itemRepository.search(itemName, page).getContent();
        List<Comment> comments = commentRepository.findAll();
        for (Item item : items) {
            List<Booking> bookingsByItem = bookings.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .collect(Collectors.toList());
            itemsDto.add(ItemMapper.toItemDto(item,
                    getLastBookingWithoutCycle(item, bookingsByItem),
                    getNextBookingWithoutCycle(item, bookingsByItem),
                    getCommentsByItemIdWithoutCycle(comments, item.getId())));
        }
        return itemsDto;
    }

    @Override
    @Transactional
    public CommentDto postComment(long userId, Long itemId, CommentDtoPost commentDtoPost) {
        checkItemsUser(userId);
        checkAvailabilityOfBookingForUser(userId, itemId);
        checkDateOfBookingForComment(userId, itemId);
        Comment comment = CommentMapper.fromCommentDtoPost(commentDtoPost,
                itemRepository.getReferenceById(itemId),
                userRepository.getReferenceById(userId),
                LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<CommentDto> getCommentsByItemIdWithoutCycle(List<Comment> comments, Long itemId) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            if (Objects.equals(comment.getItem().getId(), itemId)) {
                commentsDto.add(CommentMapper.toCommentDto(comment));
            }
        }
        return commentsDto;
    }

    private List<CommentDto> getCommentsByItemId(long itemId) {
        List<Comment> comments = Collections.emptyList();
        if (commentRepository.getItemIds().contains(itemId)) {
            comments = commentRepository.getCommentsByItemId(itemId);
        }
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toCommentDto(comment));
        }
        return commentsDto;
    }

    private void checkDateOfBookingForComment(long userId, Long itemId) {
        if (bookingRepository.getBookingsByStatusAndUserAndItemAndTime(userId, itemId).isEmpty()) {
            throw new BadRequest("Ещё не время!");
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
        Long itemFromId = itemRepository.getReferenceById(itemId).getOwner().getId();
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

    private BookingDtoForItem getLastBookingWithoutCycle(Item item, List<Booking> bookings) {
        BookingDtoForItem bookingDtoForItem = null;
        if (!item.getAvailable()) {
            return null;
        }
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

    private BookingDtoForItem getLastBooking(Item item, long userId) {
        BookingDtoForItem bookingDtoForItem = null;
        if (!item.getAvailable()) {
            return null;
        }
        List<Booking> bookings = bookingRepository.getBookingsByItemAndUserId(item.getId(), userId);
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

    private BookingDtoForItem getNextBookingWithoutCycle(Item item, List<Booking> bookings) {
        BookingDtoForItem bookingDtoForItem = null;
        if (!item.getAvailable()) {
            return null;
        }
        if (bookings.isEmpty()) {
            return null;
        }
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                bookingDtoForItem = BookingMapper.toBookingDtoForItem(booking);
                return bookingDtoForItem;
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
        if (!userRepository.existUserId(userId)) {
            throw new NotFoundException(String.format("User %d doesn't exist", userId));
        }
    }

}
