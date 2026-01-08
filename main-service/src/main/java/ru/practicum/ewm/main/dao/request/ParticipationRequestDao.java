package ru.practicum.ewm.main.dao.request;

import ru.practicum.ewm.main.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestDao {
    ParticipationRequest save(ParticipationRequest participationRequest);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    Long countByEventId(Long eventId);
}
