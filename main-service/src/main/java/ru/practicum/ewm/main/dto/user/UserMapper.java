package ru.practicum.ewm.main.dto.user;

import ru.practicum.ewm.main.model.User;

public class UserMapper {
    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToEntity(NewUserRequest requestDto) {
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        return user;
    }
}
