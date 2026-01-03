package ru.practicum.ewm.main.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.user.UserDatabaseDao;
import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.dto.user.UserMapper;
import ru.practicum.ewm.main.error.exception.ConflictException;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDatabaseDao userDao;

    @Override
    public UserDto createUser(NewUserRequest request) {
        userDao.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new ConflictException("Email уже используется: " + request.getEmail());
        });
        return UserMapper.mapToDto(userDao.save(UserMapper.mapToEntity(request)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        Page<User> users = ids == null || ids.isEmpty()
                ? userDao.findAll(pageable)
                : userDao.findByIdIn(ids, pageable);
        return users.stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        userDao.deleteById(userId);
    }
}
