package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toComment(CommentCreateDto commentCreateDto, Item item, User user, LocalDateTime time) {
        return new Comment(commentCreateDto.getText(), time, item, user);
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getCreateTime(), comment.getItem(), comment.getAuthor(), comment.getAuthor().getName());
    }
}
