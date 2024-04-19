package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments;

    public ItemBookingDto(Long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
