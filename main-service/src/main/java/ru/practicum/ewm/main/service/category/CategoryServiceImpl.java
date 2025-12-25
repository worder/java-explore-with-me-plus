package ru.practicum.ewm.main.service.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    @Override
    public CategoryDto createCategory(NewCategoryRequest request) {
        return CategoryMapper.mapToDto(categoryDao.save(CategoryMapper.mapToEntity(request)));
    }
}
