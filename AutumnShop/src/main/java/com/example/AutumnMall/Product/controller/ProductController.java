package com.example.AutumnMall.Product.controller;

import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.dto.AddProductDto;
import com.example.AutumnMall.Product.service.ImageService;
import com.example.AutumnMall.Product.service.ProductService;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestPart("addProductDto") String addProductDtoJson,  // JSON 데이터를 처리할 부분
                                              @RequestPart("image") MultipartFile image) throws JsonProcessingException {
        // JSON 문자열을 AddProductDto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);  // 컨트롤 문자 허용
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

        // JSON 문자열을 AddProductDto로 변환
        AddProductDto addProductDto = objectMapper.readValue(new String(addProductDtoJson.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8),
                AddProductDto.class);

        String imageUrl = imageService.saveImage(image);

        return ResponseEntity.ok(productService.addProduct(addProductDto, imageUrl));
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(@RequestParam(required = false, defaultValue = "0") Long categoryId,
                                                     @RequestParam(required = false, defaultValue = "0") int page) {
        int size = 10;
        if(categoryId == 0)
            return ResponseEntity.ok(productService.getProducts(page, size));
        else
            return ResponseEntity.ok(productService.getProducts(categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProducts(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // 헤더로 로그인 한 사용자만 받을 수 있음을 위 메소드와 차별화함. (오버로딩)
    @GetMapping("/getCategory/{categoryId}")
    public ResponseEntity<List<Product>> getImageUrl(@IfLogin LoginUserDto loginUserDto, @PathVariable Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }
}
