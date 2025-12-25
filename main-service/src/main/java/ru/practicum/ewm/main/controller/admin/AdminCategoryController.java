package ru.practicum.ewm.main.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryRequest;
import ru.practicum.ewm.main.service.category.CategoryService;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryRequest request) {
        return categoryService.createCategory(request);
    }
}
