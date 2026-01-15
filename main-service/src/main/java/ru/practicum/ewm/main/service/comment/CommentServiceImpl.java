package ru.practicum.ewm.main.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.comment.CommentDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.request.ParticipationRequestDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.CommentMapper;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final ParticipationRequestDao participationRequestDao;

    @Override
    public CommentDto createComment(Long userId, NewCommentRequest request) {
        User author = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User ID:" + userId + " not found"));

        Event event = eventDao.findById(request.getEventId())
                .orElseThrow(() -> new NotFoundException("Event ID:" + request.getEventId() + " not found"));

        ParticipationRequest pr = participationRequestDao.findByEventIdAndRequesterId(request.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException("User has not participated in the event"));

        if (pr.getStatus() != ParticipationRequest.Status.CONFIRMED) {
            throw new ConflictException("User participation request are not approved");
        }

        if (event.getState() != Event.EventState.PUBLISHED
                && event.getEventDate().plusHours(1).isAfter(LocalDateTime.now())) {
            throw new ConflictException("Comments can be created 1 hour after the event date");
        }

        log.debug("Creating comment from dto: {}; userId: {}", request, userId);
        Comment comment = CommentMapper.mapToComment(request, author, event);
        Comment createdComment = commentDao.save(comment);
        log.info("Created comment: {}; from dto: {}, userId: {}", createdComment, request, userId);

        return CommentMapper.mapToCommentDto(createdComment);
    }

    @Override
    public CommentDto updateComment(Long userId, UpdateCommentRequest request) {
        return null;
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
    }
}
