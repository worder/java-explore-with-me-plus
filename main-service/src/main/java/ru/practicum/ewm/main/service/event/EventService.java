package ru.practicum.ewm.main.service.event;

import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.NewEventRequest;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventRequest request);
}
