package com.example.AutumnMall.Product.controller;

import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.dto.AddProductDto;
import com.example.AutumnMall.Product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Product addProduct(@RequestBody AddProductDto addProductDto) {
        return productService.addProduct(addProductDto);
    }

    @GetMapping
    public Page<Product> getProducts(@RequestParam(required = false, defaultValue = "0") Long categoryId, @RequestParam(required = false, defaultValue = "0") int page) {
        int size = 10;
        if(categoryId == 0)
            return productService.getProducts(page, size);
        else
            return productService.getProducts(categoryId, page, size);
    }

    @GetMapping("/{id}")
    public Product getProducts(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/image/{id}")
    public Optional<Product> getImageUrl(@PathVariable Long id){
        return productService.getImageUrl(id);
    }
}
