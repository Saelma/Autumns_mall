package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Product.domain.Category;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Rating;
import com.example.AutumnMall.Product.dto.AddProductDto;
import com.example.AutumnMall.Product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public Product addProduct(AddProductDto addProductDto) {
        try {
            log.info("상품 추가 요청. 상품명: {}", addProductDto.getTitle());  // 상품 추가 요청 로그

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

            Product savedProduct = productRepository.save(product);
            log.info("상품 추가 완료. 상품 ID: {}", savedProduct.getId());  // 상품 추가 완료 로그

            return savedProduct;
        } catch (Exception e) {
            log.error("상품 추가 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(Long categoryId, int page, int size) {
        try {
            return productRepository.findProductByCategory_id(categoryId, PageRequest.of(page, size));
        } catch (Exception e) {
            log.error("상품 불러오기 실패 : {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(int page, int size) {
        try {
            return productRepository.findAll(PageRequest.of(page, size));
        } catch (Exception e) {
            log.error("페이지에 맞는 상품 불러오기 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        try {
            return productRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            log.error("해당 상품 불러오기 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Product> getImageUrl(Long id){
        try {
            return productRepository.findImageUrlById(id);
        } catch (Exception e) {
            log.error("해당 상품의 이미지 불러오기 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }
}
