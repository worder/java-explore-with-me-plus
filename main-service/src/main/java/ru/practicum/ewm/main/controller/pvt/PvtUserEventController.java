package ru.practicum.ewm.main.controller.pvt;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.NewEventRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class PvtUserEventController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventRequest request) {
        return eventService.createEvent(userId, request);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        return eventService.createEventParticipationRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId) {
        return eventService.getUserParticipationRequests(userId);
    }
}
