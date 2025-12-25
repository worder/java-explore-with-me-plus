package ru.practicum.ewm.main.dao.user;

import ru.practicum.ewm.main.model.User;

import java.util.Optional;

public interface UserDao {
    User save(User user);

    Optional<User> findById(Long id);
}
