package ru.practicum.ewm.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.compilation.CompilationDatabaseDao;
import ru.practicum.ewm.main.dao.event.EventDao;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.CompilationMapper;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationDatabaseDao compilationDao;
    private final EventDao eventDao;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = CompilationMapper.mapToCompilation(dto);
        List<Event> events = new ArrayList<>();

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = eventDao.findAllByIdIn(dto.getEvents());
            if (events.size() != dto.getEvents().size()) {
                throw new NotFoundException("Одно или несколько событий не найдены");
            }
        }

        compilation.setEvents(events);
        Compilation saved = compilationDao.save(compilation);

        Map<Long, Integer> confirmedRequests = getConfirmedRequests(saved.getEvents());
        Map<Long, Integer> views = getViews(saved.getEvents());

        return CompilationMapper.mapToDto(saved, confirmedRequests, views);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationDao.existsById(compId)) {
            throw new NotFoundException("Подборка с ID=" + compId + " не найдена");
        }
        compilationDao.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationDao.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с ID=" + compId + " не найдена"));

        CompilationMapper.updateCompilationFromRequest(compilation, request);

        if (request.getEvents() != null) {
            List<Event> events = eventDao.findAllByIdIn(request.getEvents());
            if (events.size() != request.getEvents().size()) {
                throw new NotFoundException("Одно или несколько событий не найдены");
            }
            compilation.setEvents(events);
        }

        Compilation updated = compilationDao.save(compilation);

        Map<Long, Integer> confirmedRequests = getConfirmedRequests(updated.getEvents());
        Map<Long, Integer> views = getViews(updated.getEvents());

        return CompilationMapper.mapToDto(updated, confirmedRequests, views);
    }

    private Map<Long, Integer> getConfirmedRequests(List<Event> events) {
        return events.stream().collect(Collectors.toMap(Event::getId, e -> 0));
    }

    private Map<Long, Integer> getViews(List<Event> events) {
        return events.stream().collect(Collectors.toMap(Event::getId, e -> 0));
    }
}
