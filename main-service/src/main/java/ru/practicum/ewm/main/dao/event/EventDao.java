package ru.practicum.ewm.main.dao.event;

import ru.practicum.ewm.main.model.Event;

import java.util.Optional;

public interface EventDao {
    Event save(Event event);

    Optional<Event> findById(Long id);
}
