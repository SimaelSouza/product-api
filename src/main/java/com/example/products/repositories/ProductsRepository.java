package com.example.products.repositories;

import com.example.products.models.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<ProductModel, UUID> {
    Page<ProductModel> findAll(Pageable pageable);
    Page<ProductModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ProductModel> findByPriceBetween(BigDecimal lower, BigDecimal higher, Pageable pageable);
}
