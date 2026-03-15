package com.example.products.services;

import com.example.products.dtos.PageResponseDto;
import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.exceptions.ResourceNotFoundException;
import com.example.products.models.ProductModel;
import com.example.products.repositories.ProductsRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public ProductResponseDto create (ProductCreateDto dto) {
        ProductModel productModel = new ProductModel();
        productModel.setName(dto.name());
        productModel.setPrice(dto.price());

        ProductModel saved = productsRepository.save(productModel);
        return toResponseDto(saved);
    }

    public ProductResponseDto findById(UUID id) {
        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        return toResponseDto(product);
    }

    public PageResponseDto<ProductResponseDto> searchByName(String name, Pageable pageable) {
        Page<ProductResponseDto> page = productsRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDto);
        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice.compareTo(maxPrice) > 0){
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo");
        }
        Page<ProductResponseDto> page = productsRepository
                .findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::toResponseDto);
        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findAll(Pageable pageable) {
        Page<ProductResponseDto> page = productsRepository
                .findAll(pageable)
                .map(this::toResponseDto);

        return new PageResponseDto<>(page);
    }

    @Transactional
    public ProductResponseDto update ( UUID id, ProductUpdateDto dto) {
        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        if (dto.name() == null && dto.price() == null) {
            throw new IllegalArgumentException("Pelo menos um dos campos deve ser informado");
        }
        if (dto.name() != null) {
            product.setName(dto.name());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }

        return toResponseDto(product);
    }

    @Transactional
    public void delete (UUID id) {
        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        productsRepository.delete(product);
    }

    public ProductResponseDto toResponseDto(ProductModel productModel) {
        return new ProductResponseDto (
                productModel.getId(),
                productModel.getName(),
                productModel.getPrice()
                );
    }
}
