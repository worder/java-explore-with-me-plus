package ru.practicum.ewm.main.dto.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    Long id;
    String email;
    String name;
}
