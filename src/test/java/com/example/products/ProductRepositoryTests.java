package com.example.products;

import com.example.products.models.ProductModel;
import com.example.products.repositories.ProductsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductModel buildProductModel() {
        ProductModel productModel = new ProductModel();
        productModel.setName("Placa de vídeo RTX 5090");
        productModel.setPrice(new BigDecimal("10299.99"));
        return productModel;
    }

    @Test
    @DisplayName("Deve retornar página com todos os produtos")
    void deveRetornarTodosProdutos() {
        var product1 = buildProductModel();
        entityManager.persist(product1);

        var product2 = new ProductModel();
        product2.setName("Placa mãe Mancer B450M-DA");
        product2.setPrice(new BigDecimal("399.99"));
        entityManager.persist(product2);

        Pageable pageable = PageRequest.of(0, 10);

        entityManager.flush();


        var result = productsRepository.findAll(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar produtos que contenham o nome (case insensitive)")
    void deveRetornarProdutoReferenteAoNome() {
        var product1 = buildProductModel();
        entityManager.persist(product1);

        var product2 = new ProductModel();
        product2.setName("Placa mãe Mancer B450M-DA");
        product2.setPrice(new BigDecimal("399.99"));
        entityManager.persist(product2);

        Pageable pageable = PageRequest.of(0, 10);

        entityManager.flush();

        var result = productsRepository.findByNameContainingIgnoreCase("Vídeo", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum produto corresponder ao nome")
    void deveRetornarPaginaVaziaSemCorrespondente() {
        var product1 = buildProductModel();
        entityManager.persist(product1);

        var product2 = new ProductModel();
        product2.setName("Placa mãe Mancer B450M-DA");
        product2.setPrice(new BigDecimal("399.99"));
        entityManager.persist(product2);

        Pageable pageable = PageRequest.of(0, 10);

        entityManager.flush();

        var result = productsRepository.findByNameContainingIgnoreCase("Processador", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar produtos dentro da faixa de preço")
    void deveRetornarProdutosDentroDaFaixa(){
        var product1 = buildProductModel();
        entityManager.persist(product1);

        var product2 = new ProductModel();
        product2.setName("Placa me Mancer B450M-DA");
        product2.setPrice(new BigDecimal("399.99"));
        entityManager.persist(product2);

        Pageable pageable = PageRequest.of(0, 10);

        entityManager.flush();

        var result = productsRepository.findByPriceBetween(new BigDecimal("500"), new BigDecimal("11000"), pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum produto estiver na faixa")
    void deveRetornarPaginaVaziaSemProdutosDentroDaFaixa(){
        var product1 = buildProductModel();
        entityManager.persist(product1);

        var product2 = new ProductModel();
        product2.setName("Placa me Mancer B450M-DA");
        product2.setPrice(new BigDecimal("399.99"));
        entityManager.persist(product2);

        Pageable pageable = PageRequest.of(0, 10);

        entityManager.flush();

        var result = productsRepository.findByPriceBetween(new BigDecimal("100"), new BigDecimal("200"), pageable);

        assertEquals(0, result.getTotalElements());
    }
}
