package com.example.products;

import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.mappers.ProductMapper;
import com.example.products.mappers.ProductMapperImpl;
import com.example.products.models.ProductModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ProductMapperTests {
    private final ProductMapper productMapper = new ProductMapperImpl();

    private ProductModel buildProductModel() {
        var product = new ProductModel();
        product.setName("Memória RAM 16G");
        product.setPrice(new BigDecimal("600"));
        return product;
    }

    @Test
    @DisplayName("Deve converter um ProductCreateDto para Entity corretamente")
    void deveConverterCreateDtoParaEntity() {
        var productCreate = new ProductCreateDto("Memória RAM 16G", new BigDecimal("600"));
        var product = productMapper.toModel(productCreate);

        assertAll(
                () -> assertEquals("Memória RAM 16G", product.getName()),
                () -> assertEquals(new BigDecimal("600"), product.getPrice()),
                () -> assertNull(product.getId())
        );
    }

    @Test
    @DisplayName("Deve manter atributo inalterado quando o campo do Dto for nulo")
    void deveIgnorarCampoNuloNoUpdate() {

        var product = buildProductModel();

        var productUpdate = new ProductUpdateDto(null, new BigDecimal("700"));

        productMapper.updateModelFromDto(productUpdate, product);

        assertAll(
                () -> assertEquals("Memória RAM 16G", product.getName()),
                () -> assertEquals(new BigDecimal("700"), product.getPrice())
        );
    }

    @Test
    @DisplayName("Deve atualizar nome e preço quando ambos forem fornecidos")
    void deveAlterarTodosAtributos() {
        var product = buildProductModel();

        var productUpdate = new ProductUpdateDto("Memória RAM 16G Ultra", new BigDecimal("700"));

        productMapper.updateModelFromDto(productUpdate, product);

        assertAll(
                () -> assertEquals("Memória RAM 16G Ultra", product.getName()),
                () -> assertEquals(new BigDecimal("700"), product.getPrice())
        );
    }

    @Test
    @DisplayName("Deve manter todos os atributos quando o Dto de update for completamente nulo")
    void deveManterAtributosQuandoDtoForVazio() {
        var product = buildProductModel();

        var productUpdate = new ProductUpdateDto(null, null);

        productMapper.updateModelFromDto(productUpdate, product);

        assertAll(
                () -> assertEquals("Memória RAM 16G", product.getName()),
                () -> assertEquals(new BigDecimal("600"), product.getPrice())
        );
    }

    @Test
    @DisplayName("Deve manter o id inalterado após fazer update nos atributos")
    void deveManterIdAposUpdate(){
        var product = buildProductModel();
        var id = UUID.randomUUID();
        product.setId(id);

        var productUpdate = new ProductUpdateDto("Memória RAM 16G Ultra", new BigDecimal("700"));
        productMapper.updateModelFromDto(productUpdate, product);

        assertAll(
                () -> assertEquals(id, product.getId()),
                () -> assertEquals("Memória RAM 16G Ultra", product.getName()),
                () -> assertEquals(new BigDecimal("700"), product.getPrice())
        );
    }

    @Test
    @DisplayName("Deve converter uma Entity para um ProductResponseDto corretamente")
    void deveConverterEntityParaResponseDto() {
        var product = buildProductModel();

        var responseDto = productMapper.toDto(product);
        assertAll(
                () -> assertEquals("Memória RAM 16G", responseDto.name()),
                () -> assertEquals(new BigDecimal("600"), responseDto.price()),
                () -> assertNull(responseDto.id())
        );
    }

    @Test
    @DisplayName("Deve converter uma Entity com id para um ProductResponseDto corretamente")
    void deveConverterEntityComIdParaResponseDto() {
        var product = buildProductModel();
        var id = UUID.randomUUID();
        product.setId(id);

        var responseDto = productMapper.toDto(product);

        assertAll(
                () -> assertEquals(id, responseDto.id()),
                () -> assertEquals("Memória RAM 16G", responseDto.name()),
                () -> assertEquals(new BigDecimal("600"), responseDto.price())
        );
    }

}
