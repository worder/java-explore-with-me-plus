package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "events")
public class Event {
    public static enum EventState {
        PUBLISHED,
        PENDING,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    private String description;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime eventDate;
    private double locationLat;
    private double locationLon;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;

    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
}
