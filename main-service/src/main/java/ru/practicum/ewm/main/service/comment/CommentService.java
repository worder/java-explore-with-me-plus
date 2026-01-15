package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, NewCommentRequest request);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> findUserComments(Long userId, Integer from, Integer size);

    CommentDto getComment(Long userId, Long commentId);

    List<CommentDto> getCommentsByParams(List<Long> ids,
                                          Long userId,
                                          Long eventId,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Integer from,
                                          Integer size);

    CommentDto updateCommentByAdmin(Long commentId, UpdateCommentRequest request);

    void deleteCommentByAdmin(Long commentId);
}
