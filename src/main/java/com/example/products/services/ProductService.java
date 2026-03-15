package com.example.products.services;

import com.example.products.dtos.PageResponseDto;
import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.exceptions.ResourceNotFoundException;
import com.example.products.models.ProductModel;
import com.example.products.repositories.ProductsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductsRepository productsRepository;

    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public ProductResponseDto create (ProductCreateDto dto) {
        log.info("Criando um novo produto: nome='{}', preço={}", dto.name(), dto.price());

        ProductModel productModel = new ProductModel();
        productModel.setName(dto.name());
        productModel.setPrice(dto.price());

        ProductModel saved = productsRepository.save(productModel);

        log.info("Produto salvo com sucesso. ID={}", saved.getId());
        return toResponseDto(saved);
    }

    public ProductResponseDto findById(UUID id) {
        log.debug("Procurando um produto com ID: {}", id);

        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() ->{
                        log.warn("Produto não encontrado com ID: {}", id);
                        return new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
                });

        return toResponseDto(product);
    }

    public PageResponseDto<ProductResponseDto> searchByName(String name, Pageable pageable) {
        log.debug("Procurando produtos por nome: '{}'", name);

        Page<ProductResponseDto> page = productsRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::toResponseDto);

        log.debug("Busca concluida. Total: {} itens", page.getTotalElements());
        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Procurando produtos por faixa de preço: {} - {}", minPrice, maxPrice);

        if (minPrice.compareTo(maxPrice) > 0){
            log.warn("Faixa de preço inválida: min={} max={}", minPrice, maxPrice);
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo");
        }
        Page<ProductResponseDto> page = productsRepository
                .findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::toResponseDto);

        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findAll(Pageable pageable) {
        log.debug("Listando produtos");

        Page<ProductResponseDto> page = productsRepository
                .findAll(pageable)
                .map(this::toResponseDto);

        log.debug("Total retornado: {} itens", page.getTotalElements());
        return new PageResponseDto<>(page);
    }

    @Transactional
    public ProductResponseDto update ( UUID id, ProductUpdateDto dto) {
        log.info("Atualizando produto com ID: {}", id);

        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Produto não encontrado com ID: " + id);
                });

        if (dto.name() == null && dto.price() == null) {
            log.warn("Nenhum campo informado para atualização. id={}", id);
            throw new IllegalArgumentException("Pelo menos um dos campos deve ser informado");
        }
        if (dto.name() != null) {
            product.setName(dto.name());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }

        log.info("Produto atualizado com sucesso. Id={}", id);
        return toResponseDto(product);
    }

    @Transactional
    public void delete (UUID id) {
        log.info("Removendo um produto com ID: {}", id);

        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Produto não encontrado com ID: " + id);
                });

        productsRepository.delete(product);
        log.info("Produto removido com sucesso. Id={}", id);
    }

    public ProductResponseDto toResponseDto(ProductModel productModel) {
        return new ProductResponseDto (
                productModel.getId(),
                productModel.getName(),
                productModel.getPrice()
                );
    }
}
