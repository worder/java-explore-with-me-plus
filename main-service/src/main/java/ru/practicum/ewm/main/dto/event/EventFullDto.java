package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.model.Event;

import java.time.LocalDateTime;

@Value
@Builder
public class EventFullDto {
    Long id;
    String annotation;
    String title;
    String description;

    Event.EventState state;

    UserDto initiator;
    CategoryDto category;
    EventLocationDto location;

    Integer confirmedRequests;
    Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    Boolean paid;
    Boolean requestModeration;

    Integer views;
}
