package ru.practicum.ewm.main.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.request.ParticipationRequestDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestMapper;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.ParticipationRequestStatusUpdateResultDto;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final EventDao eventDao;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;

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

        if (participationRequestDao.countByEventIdAndStatus(eventId, ParticipationRequest.Status.CONFIRMED)
                .equals(event.getParticipantLimit())) {
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
    public ParticipationRequestDto cancelEventParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = participationRequestDao.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        if (!request.getRequesterId().equals(userId)) {
            throw new NotFoundException("Invalid user id");
        }

        if (request.getStatus() !=  ParticipationRequest.Status.PENDING) {
            throw new ConflictException("Can not cancel approved request");
        }

        request.setStatus(ParticipationRequest.Status.CANCELED);
        ParticipationRequest savedRequest = participationRequestDao.save(request);
        return ParticipationRequestMapper.mapToParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getUserParticipationRequests(Long userId) {
        return participationRequestDao.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsForUserEvent(Long userId, Long eventId) {
        Event userEvent = eventDao.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!userEvent.getUser().getId().equals(userId)) {
            throw new ConflictException("Invalid user id");
        }

        return participationRequestDao.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestStatusUpdateResultDto updateParticipationRequestsStatus(
            Long userId,
            Long eventId,
            ParticipationRequestStatusUpdateRequest request) {
        Event event = eventDao.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        int confirmedRequests = participationRequestDao.countByEventIdAndStatus(eventId, ParticipationRequest.Status.CONFIRMED);
        int slotsAvailable = event.getParticipantLimit() - confirmedRequests;

        if (slotsAvailable == 0) {
            throw new ConflictException("Participation requests limit has reached for the event");
        }

        List<ParticipationRequest> approvedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        int confirmations = 0;

        List<ParticipationRequest> requests = participationRequestDao.findByIdIn(request.getRequestIds());
        for (ParticipationRequest r : requests) {
            if (!r.getStatus().equals(ParticipationRequest.Status.PENDING)) {
                throw new ConflictException("Participation request ID: " + r.getId() + " must be in pending status");
            }

            if (request.getStatus()
                    .equals(ParticipationRequestStatusUpdateRequest.ParticipationRequestStatusUpdate.CONFIRMED)
                    && slotsAvailable > 0) {
                r.setStatus(ParticipationRequest.Status.CONFIRMED);
                approvedRequests.add(r);
                slotsAvailable--;
                confirmations++;
            } else {
                r.setStatus(ParticipationRequest.Status.REJECTED);
                rejectedRequests.add(r);
            }

            participationRequestDao.save(r);
        }

        // TODO update event confirmed requests

        return new ParticipationRequestStatusUpdateResultDto(
                approvedRequests.stream().map(ParticipationRequestMapper::mapToParticipationRequestDto).toList(),
                rejectedRequests.stream().map(ParticipationRequestMapper::mapToParticipationRequestDto).toList());
    }
}
