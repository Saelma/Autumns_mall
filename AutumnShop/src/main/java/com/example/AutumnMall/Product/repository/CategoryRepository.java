package com.example.AutumnMall.Product.repository;

import com.example.AutumnMall.Product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
