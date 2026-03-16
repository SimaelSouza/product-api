package com.example.products.services;

import com.example.products.dtos.PageResponseDto;
import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.exceptions.ResourceNotFoundException;
import com.example.products.mappers.ProductMapper;
import com.example.products.models.ProductModel;
import com.example.products.repositories.ProductsRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductMapper productMapper;

    public ProductService(ProductsRepository productsRepository, ProductMapper productMapper) {
        this.productsRepository = productsRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDto create (ProductCreateDto dto) {
        log.info("Criando um novo produto: nome='{}', preço={}", dto.name(), dto.price());

        ProductModel productModel = productMapper.toModel(dto);

        ProductModel saved = productsRepository.save(productModel);

        log.info("Produto salvo com sucesso. ID={}", saved.getId());
        return productMapper.toDto(saved);
    }

    public ProductResponseDto findById(UUID id) {
        log.debug("Procurando um produto com ID: {}", id);

        ProductModel product = productsRepository.findById(id)
                .orElseThrow(() ->{
                        log.warn("Produto não encontrado com ID: {}", id);
                        return new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
                });

        return productMapper.toDto(product);
    }

    public PageResponseDto<ProductResponseDto> searchByName(String name, Pageable pageable) {
        log.debug("Procurando produtos por nome: '{}'", name);

        if (name == null || name.isBlank()) {
            return new PageResponseDto<>(Page.empty(pageable));
        }

        Page<ProductResponseDto> page = productsRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(productMapper::toDto);

        log.debug("Busca concluida. Total: {} itens", page.getTotalElements());
        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Procurando produtos por faixa de preço: {} - {}", minPrice, maxPrice);

        if(minPrice == null || maxPrice == null){
            throw new IllegalArgumentException("Valores de preço não podem ser nulos");
        }

        if (minPrice.compareTo(maxPrice) > 0){
            log.warn("Faixa de preço inválida: min={} max={}", minPrice, maxPrice);
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o valor máximo");
        }

        Page<ProductResponseDto> page = productsRepository
                .findByPriceBetween(minPrice, maxPrice, pageable)
                .map(productMapper::toDto);

        return new PageResponseDto<>(page);
    }

    public PageResponseDto<ProductResponseDto> findAll(Pageable pageable) {
        log.debug("Listando produtos");

        Page<ProductResponseDto> page = productsRepository
                .findAll(pageable)
                .map(productMapper::toDto);

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

        productMapper.updateModelFromDto(dto, product);

        log.info("Produto atualizado com sucesso. Id={}", id);

        return productMapper.toDto(product);
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
}
