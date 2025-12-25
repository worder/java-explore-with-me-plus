package ru.practicum.ewm.main.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.user.UserDao;
import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.dto.user.UserMapper;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;


    @Override
    public UserDto createUser(NewUserRequest request) {
        return UserMapper.mapToDto(userDao.save(UserMapper.mapToEntity(request)));
    }
}
