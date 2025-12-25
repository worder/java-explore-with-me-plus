package ru.practicum.ewm.main.service.user;

import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;

public interface UserService {
    UserDto createUser(NewUserRequest request);
}
