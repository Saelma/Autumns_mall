package com.example.AutumnMall.Product.controller;

import com.example.AutumnMall.Product.domain.Category;
import com.example.AutumnMall.Product.dto.AddCategoryDto;
import com.example.AutumnMall.Product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody AddCategoryDto addCategoryDto){
        return categoryService.addCategory(addCategoryDto);
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.getCategories();
    }
}
