package ru.practicum.ewm.main.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.service.user.UserService;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }
}
