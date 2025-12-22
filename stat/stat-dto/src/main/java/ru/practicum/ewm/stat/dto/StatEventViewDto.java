package ru.practicum.ewm.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class StatEventViewDto {
    String app;
    String uri;
    Long hits;
}
