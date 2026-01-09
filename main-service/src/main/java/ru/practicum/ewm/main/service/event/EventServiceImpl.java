package ru.practicum.ewm.main.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.request.ParticipationRequestDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestMapper;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.ForbiddenException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventRequest request) {
        log.info("Создание события пользователем с ID: {}", userId);

        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        Category category = categoryDao.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        Event event = EventMapper.mapToEvent(request, category, user);
        Event savedEvent = eventDao.save(event);

        log.info("Создано событие с ID: {}", savedEvent.getId());
        return EventMapper.mapToFullDto(savedEvent, 0, 0);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя с ID: {}, from={}, size={}", userId, from, size);

        userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventDao.findAllByUserId(userId, pageable);

        return events.stream()
                .map(event -> EventMapper.mapToShortDto(event, 0, 0))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события с ID: {} пользователя с ID: {}", eventId, userId);

        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID=%d пользователя с ID=%d не найдено", eventId, userId)));

        return EventMapper.mapToFullDto(event, 0, 0);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Обновление события с ID: {} пользователем с ID: {}", eventId, userId);

        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID=%d пользователя с ID=%d не найдено", eventId, userId)));

        if (event.getState() == Event.EventState.PUBLISHED) {
            throw new ForbiddenException("Нельзя изменить опубликованное событие");
        }

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 2 часа от текущего момента");
        }

        Category category = null;
        if (request.getCategory() != null) {
            category = categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromUserRequest(event, request, category);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(Event.EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(Event.EventState.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventDao.save(event);
        log.info("Событие с ID: {} обновлено пользователем", eventId);

        return EventMapper.mapToFullDto(updatedEvent, 0, 0);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {
        log.info("Получение событий администратором с параметрами: users={}, states={}, categories={}",
                users, states, categories);

        List<Event.EventState> stateEnums = null;
        if (states != null) {
            stateEnums = states.stream()
                    .map(Event.EventState::valueOf)
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventDao.findAllByParams(users, stateEnums, categories, rangeStart, rangeEnd, pageable);

        return events.stream()
                .map(event -> EventMapper.mapToFullDto(event, 0, 0))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        log.info("Обновление события с ID: {} администратором", eventId);

        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + eventId + " не найдено"));

        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Дата события должна быть не ранее чем за 1 час от текущего момента");
        }

        if (request.getStateAction() != null) {
            if (event.getState() != Event.EventState.PENDING) {
                throw new ForbiddenException(
                        String.format("Событие должно быть в состоянии PENDING. Текущее состояние: %s",
                                event.getState()));
            }

            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(Event.EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(Event.EventState.CANCELED);
                    break;
            }
        }

        Category category = null;
        if (request.getCategory() != null) {
            category = categoryDao.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ID=" + request.getCategory() + " не найдена"));
        }

        EventMapper.updateEventFromAdminRequest(event, request, category);

        Event updatedEvent = eventDao.save(event);
        log.info("Событие с ID: {} обновлено администратором", eventId);

        return EventMapper.mapToFullDto(updatedEvent, 0, 0);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort,
                                               Integer from, Integer size) {
        log.info("Получение публичных событий с параметрами: text={}, categories={}, paid={}",
                text, categories, paid);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventDao.findAllPublicByParams(text, categories, paid, rangeStart, rangeEnd, pageable);

        return events.stream()
                .map(event -> EventMapper.mapToShortDto(event, 0, 0))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        log.info("Получение публичного события с ID: {}", id);

        Event event = eventDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + id + " не найдено"));

        if (event.getState() != Event.EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }

        return EventMapper.mapToFullDto(event, 0, 0);
    }

    @Override
    public ParticipationRequestDto createEventParticipationRequest(Long userId, Long eventId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        participationRequestDao.findByEventIdAndRequesterId(eventId, userId).ifPresent(r -> {
            throw new ConflictException("User already sent participation request");
        });

        if (event.getUser().getId().equals(user.getId())) {
            throw new ConflictException("User can not participate to it's own event");
        }

        if (!event.getState().equals(Event.EventState.PUBLISHED)) {
            throw new ConflictException("Can not participate, event is not published");
        }

        if (participationRequestDao.countByEventId(eventId).equals(event.getParticipantLimit())) {
            throw new ConflictException("Participation limit reached for the event");
        }

        ParticipationRequest model = new ParticipationRequest();
        model.setRequesterId(userId);
        model.setEventId(eventId);
        model.setStatus(event.getRequestModeration() ?
                ParticipationRequest.Status.PENDING :
                ParticipationRequest.Status.CONFIRMED);
        model.setCreatedOn(LocalDateTime.now());

        ParticipationRequest savedModel = participationRequestDao.save(model);

        return ParticipationRequestMapper.mapToParticipationRequestDto(savedModel);
    }

    @Override
    public List<ParticipationRequestDto> getUserParticipationRequests(Long userId) {
        return participationRequestDao.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }
}