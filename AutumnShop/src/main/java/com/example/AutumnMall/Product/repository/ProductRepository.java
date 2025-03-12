package com.example.AutumnMall.Product.repository;

import com.example.AutumnMall.Product.domain.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductByCategory_id(Long categoryId, Pageable pageable);

    List<Product> findByCategory_id(Long categoryId);

    Optional<Product> findImageUrlById(Long id);

    @NotNull
    Optional<Product> findById(@NotNull Long id);

    // 물품 개수 업데이트 시 비관적 락을 이용함
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithLock(@Param("productId") Long productId);

}
