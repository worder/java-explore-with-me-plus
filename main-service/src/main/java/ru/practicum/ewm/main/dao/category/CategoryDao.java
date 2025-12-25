package ru.practicum.ewm.main.dao.category;

import ru.practicum.ewm.main.model.Category;

import java.util.Optional;

public interface CategoryDao {
    Category save(Category category);

    Optional<Category> findById(Long id);
}
