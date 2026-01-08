package ru.practicum.ewm.main.service.event;

import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.NewEventRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventRequest request);

    ParticipationRequestDto createEventParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserParticipationRequests(Long userId);
}
