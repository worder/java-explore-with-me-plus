package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;

public interface CommentService {
    CommentDto createComment(Long userId, NewCommentRequest request);

    CommentDto updateComment(Long userId, UpdateCommentRequest request);

    void deleteComment(Long userId, Long commentId);
}
