package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Product.domain.Category;
import com.example.AutumnMall.Product.dto.AddCategoryDto;
import com.example.AutumnMall.Product.repository.CategoryRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category addCategory(AddCategoryDto addCategoryDto){
        try {
            Category category = new Category();
            category.setName(addCategoryDto.getName());

            return categoryRepository.save(category);
        } catch (Exception e) {
            log.error("카테고리 추가 실패 {} ", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            log.error("모든 카테고리 불러오기 실패 {} ", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Category getCategory(Long categoryId) {
        try {
            return categoryRepository.findById(categoryId).orElseThrow();
        } catch (Exception e) {
            log.error("해당 카테고리 불러오기 실패 {} ", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
