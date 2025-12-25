package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewEventRequest {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private EventLocationDto location;

    @NotNull
    private Boolean paid;

    @Positive
    private Integer participantLimit;

    @NotNull
    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
