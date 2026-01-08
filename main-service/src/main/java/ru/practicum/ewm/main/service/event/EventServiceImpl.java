package ru.practicum.ewm.main.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.request.ParticipationRequestDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.dto.event.NewEventRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestMapper;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;

    @Override
    public EventFullDto createEvent(Long userId, NewEventRequest request) {
        if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Invalid date");
        }

        Category category = categoryDao.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return EventMapper.mapToFullDto(eventDao.save(EventMapper.mapToEvent(request, category, user)), 0, 0);
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

        if (!event.getState().equals(ParticipationRequest.Status.CONFIRMED)) {
            throw new ConflictException("Can not participate, event is not approved");
        }

        if (participationRequestDao.countByEventId(eventId) == event.getParticipantLimit()) {
            throw new ConflictException("Participation limit reached for the event");
        }

        ParticipationRequest model = new ParticipationRequest();
        model.setRequesterId(userId);
        model.setEventId(eventId);
        model.setStatus(event.isRequestModeration() ?
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
