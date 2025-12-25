package ru.practicum.ewm.main.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.dto.event.NewEventRequest;
import ru.practicum.ewm.main.error.exception.BadRequestException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;

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
}
