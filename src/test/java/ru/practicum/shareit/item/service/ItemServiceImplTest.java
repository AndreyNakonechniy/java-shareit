package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    User owner = new User(1L, "name", "email@email.ru");
    User author = new User(2L, "author", "email@email.ru");
    Item item = new Item(1L, "name", "description", true, owner, null);
    Item lastItem = new Item(2L, "name2", "description2", true, owner, null);
    Item nextItem = new Item(3L, "name3", "description3", true, owner, null);
    ItemCreateDto itemCreateDto = new ItemCreateDto("name", "description", true, null);
    ItemUpdateDto itemUpdateDto = new ItemUpdateDto("newName", "newDescription", true);
    BookingItemDto lastBooking = new BookingItemDto(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(12), lastItem, owner, 1L, BookingStatus.APPROVED);
    BookingItemDto nextBooking = new BookingItemDto(2L, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(22), nextItem, owner, 1L, BookingStatus.APPROVED);
    CommentDto comment = new CommentDto(1L, "text", LocalDateTime.now(), item, author, "author");
    CommentCreateDto commentCreateDto = new CommentCreateDto("text");
    ItemBookingDto itemBookingDto = new ItemBookingDto(1L, "name", "description", true, owner, lastBooking, nextBooking, List.of(comment));
    ItemMapper mapper = new ItemMapper();
    private static final LocalDateTime testTime = LocalDateTime.of(2023, 12, 12, 12, 12);
    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    private static MockedStatic<LocalDateTime> mockTime;

    @BeforeAll
    public static void changeTime() {
        mockTime = Mockito.mockStatic(LocalDateTime.class);
        mockTime.when(LocalDateTime::now).thenReturn(testTime);
    }

    @AfterAll
    static void close() {
        mockTime.close();
    }

    @Test
    void create() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.save(mapper.toItem(itemCreateDto, owner))).thenReturn(item);
        assertEquals(mapper.toItemDto(item), itemService.create(1L, itemCreateDto));
        verify(itemRepository).save(mapper.toItem(itemCreateDto, owner));
    }

    @Test
    void createWithException() {
        assertThrows(NotFoundException.class, () -> itemService.create(0L, itemCreateDto));
    }

    @Test
    void update() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));

        Item itemToCheck = new Item(1L, "newName", "newDescription", true, owner, null);
        ItemDto newItemDto = itemService.update(1L, 1L, itemUpdateDto);

        assertEquals(mapper.toItemDto(itemToCheck), newItemDto);
        verify(itemRepository).save(itemToCheck);
    }

    @Test
    void updateWithUserNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.update(0L, 1L, itemUpdateDto));
    }

    @Test
    void updateWithItemNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        assertThrows(NotFoundException.class, () -> itemService.update(1L, 0L, itemUpdateDto));
    }

    @Test
    void updateWithUserWithNoItem() {
        User userTest = new User(3L, "name", "email@mail.ru");
        when(userRepository.findById(3L)).thenReturn(Optional.of(userTest));
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> itemService.update(3L, 1L, itemUpdateDto));
    }

    @Test
    void getById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Booking currentLastBooking = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(12), lastItem, owner, BookingStatus.APPROVED);
        Booking currentNextBooking = new Booking(2L, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(22), nextItem, owner, BookingStatus.APPROVED);
        Comment currentComment = new Comment(1L, "text", LocalDateTime.now(), item, author);

        when(bookingRepository.findAllByItemIdOrderByStart(1L)).thenReturn(List.of(currentLastBooking, currentNextBooking));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(currentComment));

        ItemBookingDto itemBookingDtoToCheck = itemService.getById(1L, 1L);
        assertEquals(itemBookingDto, itemBookingDtoToCheck);
    }

    @Test
    void getByIdWithEmptyLastAndNextBooking() {
        ItemBookingDto itemBookingDtoTest = new ItemBookingDto(1L, "name", "description", true, owner, null, null, List.of(comment));

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Comment currentComment = new Comment(1L, "text", LocalDateTime.now(), item, author);

        when(bookingRepository.findAllByItemIdOrderByStart(1L)).thenReturn(List.of());
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(currentComment));

        ItemBookingDto itemBookingDtoToCheck = itemService.getById(1L, 1L);
        assertEquals(itemBookingDtoTest, itemBookingDtoToCheck);
    }

    @Test
    void getByIdWithUserNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.getById(0L, 1L));
    }

    @Test
    void getByIdWithItemNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 0L));
    }

    @Test
    void getOwnerItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findByOwnerId(1L, defaultPageRequest)).thenReturn(List.of(item));

        Booking currentLastBooking = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(12), lastItem, owner, BookingStatus.APPROVED);
        Booking currentNextBooking = new Booking(2L, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(22), nextItem, owner, BookingStatus.APPROVED);
        Comment currentComment = new Comment(1L, "text", LocalDateTime.now(), item, author);

        when(bookingRepository.findAllByItemIdOrderByStart(1L)).thenReturn(List.of(currentLastBooking, currentNextBooking));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(currentComment));

        List<ItemBookingDto> itemBookingDtos = itemService.getOwnerItems(1L, 0, 10);
        assertEquals(1, itemBookingDtos.size());
        assertEquals(List.of(itemBookingDto), itemBookingDtos);
    }

    @Test
    void getOwnerItemsWithEmptyLastAndNextBooking() {
        ItemBookingDto itemBookingDtoTest = new ItemBookingDto(1L, "name", "description", true, owner, null, null, List.of(comment));

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findByOwnerId(1L, defaultPageRequest)).thenReturn(List.of(item));

        Comment currentComment = new Comment(1L, "text", LocalDateTime.now(), item, author);

        when(bookingRepository.findAllByItemIdOrderByStart(1L)).thenReturn(List.of());
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(currentComment));

        List<ItemBookingDto> itemBookingDtos = itemService.getOwnerItems(1L, 0, 10);
        assertEquals(1, itemBookingDtos.size());
        assertEquals(List.of(itemBookingDtoTest), itemBookingDtos);
    }

    @Test
    void getOwnerItemsWithException() {
        assertThrows(NotFoundException.class, () -> itemService.getOwnerItems(0L, 0, 10));
    }

    @Test
    void search() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        when(itemRepository.search("description", defaultPageRequest)).thenReturn(List.of(item));
        List<ItemDto> items = itemService.search(1L, "description", 0, 10);

        assertEquals(1, items.size());
        assertEquals(mapper.toItemDto(item).getName(), items.get(0).getName());
    }

    @Test
    void searchWithException() {
        assertThrows(NotFoundException.class, () -> itemService.search(0L, "description", 0, 10));
    }

    @Test
    void addComment() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(author));

        Item currentItem = new Item(1L, "name", "desc", true, author, null);
        Booking currentBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(12), currentItem, author, BookingStatus.APPROVED);

        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(2L, LocalDateTime.now())).thenReturn(List.of(currentBooking));
        Comment currentComment = new Comment("text", LocalDateTime.now(), item, author);
        Comment commentToCheck = new Comment(1L, "text", LocalDateTime.now(), item, author);
        when(commentRepository.save(currentComment)).thenReturn(commentToCheck);

        assertEquals(comment, itemService.addComment(2L, 1L, commentCreateDto));
    }

    @Test
    void addCommentWithItemNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 0L, commentCreateDto));
    }

    @Test
    void addCommentWithUserNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> itemService.addComment(0L, 1L, commentCreateDto));
    }
}