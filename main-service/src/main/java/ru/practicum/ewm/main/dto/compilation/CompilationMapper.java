package ru.practicum.ewm.main.dto.compilation;

import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.main.model.Compilation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto mapToDto(Compilation compilation, Map<Long, Integer> confirmedRequests, Map<Long, Integer> views) {
        List<EventShortDto> eventDtos = compilation.getEvents() == null ? Collections.emptyList() :
                compilation.getEvents().stream()
                        .map(event -> EventMapper.mapToShortDto(event,
                                confirmedRequests.getOrDefault(event.getId(), 0),
                                views.getOrDefault(event.getId(), 0)))
                        .collect(Collectors.toList());

        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        dto.setEvents(eventDtos);
        return dto;
    }

    public static Compilation mapToCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        return compilation;
    }

    public static void updateCompilationFromRequest(Compilation compilation, UpdateCompilationRequest request) {
        if (request.getTitle() != null) compilation.setTitle(request.getTitle());
        if (request.getPinned() != null) compilation.setPinned(request.getPinned());
    }
}
