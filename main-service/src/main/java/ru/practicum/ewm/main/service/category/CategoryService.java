package ru.practicum.ewm.main.service.category;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.dto.category.UpdateCategoryRequest;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryRequest request);

    CategoryDto updateCategory(Long catId, UpdateCategoryRequest request);

    void deleteCategory(Long catId);
}
