package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Product.domain.Category;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Rating;
import com.example.AutumnMall.Product.dto.AddProductDto;
import com.example.AutumnMall.Product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public Product addProduct(AddProductDto addProductDto) {
        Category category = categoryService.getCategory(addProductDto.getCategoryId());
        Product product = new Product();
        product.setCategory(category);
        product.setPrice(addProductDto.getPrice());
        product.setDescription(addProductDto.getDescription());
        product.setImageUrl(addProductDto.getImageUrl());
        product.setTitle(addProductDto.getTitle());
        Rating rating = new Rating();
        rating.setRate(0.0);
        rating.setCount(0);
        product.setRating(rating);

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(Long categoryId, int page, int size) {
        return productRepository.findProductByCategory_id(categoryId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getImageUrl(Long id){
        return productRepository.findImageUrlById(id);
    }
}
