package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TestEntityManager testEntityManager;

    User owner = new User("name", "email@email.ru");
    User author = new User("name2", "email@email.com");
    Item item = new Item("name", "description", true, owner, null);

    Comment comment = new Comment("text", LocalDateTime.now(), item, author);

    @BeforeEach
    void setUp() {
        testEntityManager.persist(owner);
        testEntityManager.persist(author);
        testEntityManager.persist(item);
        testEntityManager.flush();
        commentRepository.save(comment);
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(1L);

        assertEquals(1, comments.size());
        assertEquals("name", comments.get(0).getItem().getName());
    }
}