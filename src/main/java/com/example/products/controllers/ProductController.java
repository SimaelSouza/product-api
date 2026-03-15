package com.example.products.controllers;

import com.example.products.dtos.PageResponseDto;
import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity <ProductResponseDto> saveProduct(@RequestBody @Valid ProductCreateDto productCreateDto) {
        var response= productService.create(productCreateDto);
        URI location = URI.create("/products/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity <PageResponseDto<ProductResponseDto>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity <ProductResponseDto> getOneProduct(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity <PageResponseDto<ProductResponseDto>> searchProductsByName(@RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(productService.searchByName(name, pageable));
    }

    @GetMapping("/price")
    public ResponseEntity <PageResponseDto<ProductResponseDto>> searchProductsByValue(@RequestParam BigDecimal minPrice , @RequestParam BigDecimal maxPrice, Pageable pageable) {
        return ResponseEntity.ok(productService.findByPriceBetween(minPrice, maxPrice, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity <ProductResponseDto> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductUpdateDto productUpdateDto) {
        return ResponseEntity.ok(productService.update(id, productUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(value = "id") UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
