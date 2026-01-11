package ru.practicum.ewm.main.dto.compilation;

import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.model.Compilation;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto mapToDto(Compilation compilation, java.util.Map<Long, Integer> confirmedRequests, java.util.Map<Long, Integer> views) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream()
                        .map(event -> EventMapper.mapToShortDto(event,
                                confirmedRequests.getOrDefault(event.getId(), 0),
                                views.getOrDefault(event.getId(), 0)))
                        .collect(Collectors.toList()))
                .build();
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
