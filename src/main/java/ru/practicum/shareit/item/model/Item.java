package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;

    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

}
