package ru.practicum.ewm.main.service.category;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryRequest request);
}
