package ru.practicum.ewm.main.dto.event;

import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.user.UserMapper;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;

public class EventMapper {
    public static EventFullDto mapToFullDto(Event event, Integer confirmedRequests, Integer views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .description(event.getDescription())
                .state(event.getState())
                .initiator(UserMapper.mapToDto(event.getUser()))
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .location(EventLocationDto.builder()
                        .lat(event.getLocationLat())
                        .lon(event.getLocationLon())
                        .build())
                .confirmedRequests(confirmedRequests)
                .participantLimit(event.getParticipantLimit())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .paid(event.isPaid())
                .requestModeration(event.isRequestModeration())
                .views(views)
                .build();
    }

    public static Event mapToEvent(NewEventRequest request, Category category, User user) {
        Event event = new Event();
        event.setCategory(category);
        event.setUser(user);
        event.setState(Event.EventState.PENDING);

        event.setTitle(request.getTitle());
        event.setAnnotation(request.getAnnotation());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setParticipantLimit(request.getParticipantLimit());

        event.setLocationLat(request.getLocation().getLat());
        event.setLocationLon(request.getLocation().getLon());

        event.setPaid(request.getPaid());
        event.setRequestModeration(request.getRequestModeration());

        event.setCreatedOn(LocalDateTime.now());

        return event;
    }
}
