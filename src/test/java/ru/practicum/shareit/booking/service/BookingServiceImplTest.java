package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
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
class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    BookingMapper mapper = new BookingMapper();

    User owner = new User(1L, "name", "email@email.ru");
    User booker = new User(2L, "name", "email@email.ru");
    Item item = new Item(1L, "name", "description", true, owner, null);
    Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
    BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

    static LocalDateTime testTime = LocalDateTime.of(2023, 12, 12, 12, 12);
    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    private static MockedStatic<LocalDateTime> mockTime;

    @BeforeAll
    static void changeTime() {
        mockTime = Mockito.mockStatic(LocalDateTime.class);
        mockTime.when(LocalDateTime::now).thenReturn(testTime);
    }

    @AfterAll
    static void close() {
        mockTime.close();
    }

    @Test
    void create() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(mapper.bookingCreate(bookingCreateDto, booker, item))).thenReturn(booking);

        assertEquals(mapper.toBookingDto(booking), bookingService.create(2L, bookingCreateDto));
        verify(bookingRepository).save(mapper.bookingCreate(bookingCreateDto, booker, item));
    }

    @Test
    void createWithUserNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.create(0L, bookingCreateDto));
    }

    @Test
    void createWithItemNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));
        BookingCreateDto testBooking = new BookingCreateDto(0L, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, testBooking));
    }

    @Test
    void update() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.save(booking)).thenReturn(testBooking);

        assertEquals(mapper.toBookingDto(testBooking), bookingService.update(1L, 1L, true));
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void updateWithBookingNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.update(1L, 0L, true));
    }

    @Test
    void updateWithUserNotOwnerException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.update(2L, 1L, true));
    }

    @Test
    void updateWithValidationException() {
        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        assertThrows(ValidationException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void getBookingById() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));

        BookingDto bookingDto = bookingService.getBookingById(2L, 1L);
        assertEquals(mapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void getBookingByIdWithUserNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(0L, 1L));
    }

    @Test
    void getBookingByIdWithBookingNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(owner));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, 0L));
    }

    @Test
    void getAllWithStateAll() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(2L, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "ALL", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithStateCurrent() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(2L, LocalDateTime.now(), LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "CURRENT", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithStatePast() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(2L, LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "PAST", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithStateFuture() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(2L, LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "FUTURE", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithStateWaiting() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(2L, LocalDateTime.now(), BookingStatus.WAITING, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "WAITING", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithStateRejected() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.REJECTED);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAll(2L, "REJECTED", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getItem().getOwner().getId(), bookingDtos.get(0).getItem().getOwner().getId());
    }

    @Test
    void getAllWithNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAll(0L, "ALL", 0, 10));
    }

    @Test
    void getAllWithUnsupportedStatusException() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAll(2L, "state", 0, 10));
    }

    @Test
    void getAllOwnerWithStateAll() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "ALL", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithStateCurrent() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(1L, LocalDateTime.now(), LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "CURRENT", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithStatePast() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(1L, LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "PAST", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithStateFuture() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(1L, LocalDateTime.now(), defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "FUTURE", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithStateWaiting() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(1L, LocalDateTime.now(), BookingStatus.WAITING, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "WAITING", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithStateRejected() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));

        Booking testBooking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.REJECTED);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED, defaultPageRequest)).thenReturn(List.of(testBooking));
        List<BookingDto> bookingDtos = bookingService.getAllOwner(1L, "REJECTED", 0, 10);
        assertEquals(1, bookingDtos.size());
        assertEquals(mapper.toBookingDto(testBooking).getBooker().getId(), bookingDtos.get(0).getBooker().getId());
    }

    @Test
    void getAllOwnerWithNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllOwner(0L, "ALL", 0, 10));
    }

    @Test
    void getAllOwnerWithUnsupportedStatusException() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllOwner(2L, "state", 0, 10));
    }

    @Test
    void bookingValidationItemNotAvailable() {
        Item itemTest = new Item(1L, "name", "description", false, owner, null);
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemTest));

        assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingCreateDto));
    }

    @Test
    void bookingValidationOwnerIsBooker() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingCreateDto));
    }

    @Test
    void bookingValidationWrongDate() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        BookingCreateDto bookingCreateDtoTest = new BookingCreateDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1));

        assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingCreateDtoTest));
    }
}