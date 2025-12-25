package ru.practicum.ewm.main.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.User;

@Repository
public interface UserDatabaseDao extends UserDao, JpaRepository<User, Long> {
}
