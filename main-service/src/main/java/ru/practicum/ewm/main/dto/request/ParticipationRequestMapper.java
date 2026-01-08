package ru.practicum.ewm.main.dto.request;

import ru.practicum.ewm.main.model.ParticipationRequest;

import java.time.LocalDateTime;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest model) {
        return ParticipationRequestDto.builder()
                .id(model.getId())
                .event(model.getEventId())
                .requester(model.getRequesterId())
                .status(model.getStatus().name())
                .created(model.getCreatedOn())
                .build();
    }
}
