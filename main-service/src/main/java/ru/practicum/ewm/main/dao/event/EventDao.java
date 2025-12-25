package ru.practicum.ewm.main.dao.event;

import ru.practicum.ewm.main.model.Event;

public interface EventDao {
    Event save(Event event);
}
