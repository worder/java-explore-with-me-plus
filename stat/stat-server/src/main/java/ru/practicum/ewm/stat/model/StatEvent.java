package ru.practicum.ewm.stat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "stat_events")
public class StatEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String app;
    String uri;
    String ip;

    @Column(name = "event_date")
    LocalDateTime date;
}
