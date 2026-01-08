package ru.practicum.ewm.main.dao.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.Category;

import java.util.Optional;

public interface CategoryDatabaseDao extends CategoryDao, JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Override
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id")
    void deleteById(Long id);
}
