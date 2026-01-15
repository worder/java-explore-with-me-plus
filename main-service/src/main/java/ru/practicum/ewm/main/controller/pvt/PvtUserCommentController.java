package ru.practicum.ewm.main.controller.pvt;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@AllArgsConstructor
public class PvtUserCommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable Long userId,
                             @RequestBody NewCommentRequest request) {
        return commentService.createComment(userId, request);
    }
}
