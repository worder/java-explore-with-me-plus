package ru.practicum.ewm.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dao.category.CategoryDao;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.dto.category.UpdateCategoryRequest;
import ru.practicum.ewm.main.error.exception.NotFoundException;
import ru.practicum.ewm.main.model.Category;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryRequest request) {
        Category category = CategoryMapper.mapToEntity(request);
        return CategoryMapper.mapToDto(categoryDao.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, UpdateCategoryRequest request) {
        Category category = getCategoryOrThrow(catId);

        if (request.getName() != null) {
            Optional<Category> existing = categoryDao.findByName(request.getName());
            if (existing.isPresent() && !existing.get().getId().equals(catId)) {
                throw new RuntimeException("Category with name '" + request.getName() + "' already exists.");
            }
            category.setName(request.getName());
        }

        return CategoryMapper.mapToDto(categoryDao.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        getCategoryOrThrow(catId);
        categoryDao.deleteById(catId);
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryDao.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found."));
    }
}
