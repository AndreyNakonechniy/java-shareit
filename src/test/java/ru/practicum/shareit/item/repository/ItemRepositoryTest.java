package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    User user = new User("name", "email@email.ru");
    Item item = new Item("name", "description", true, user);
    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        testEntityManager.persist(user);
        testEntityManager.flush();
        itemRepository.save(item);
    }

    @Test
    void findByOwnerId() {
        List<Item> checkItem = itemRepository.findByOwnerId(1L, defaultPageRequest);

        assertEquals(1, checkItem.size());
        assertEquals("name", checkItem.get(0).getName());
    }

    @Test
    void search() {
        List<Item> checkItem = itemRepository.search("dEScriPtiOn", defaultPageRequest);

        assertEquals(1, checkItem.size());
        assertEquals("name", checkItem.get(0).getName());
    }
}