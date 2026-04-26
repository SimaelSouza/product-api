package com.example.products;

import com.example.products.dtos.ProductCreateDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.dtos.ProductUpdateDto;
import com.example.products.exceptions.ResourceNotFoundException;
import com.example.products.mappers.ProductMapper;
import com.example.products.models.ProductModel;
import com.example.products.repositories.ProductsRepository;
import com.example.products.services.ProductService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    ProductsRepository productsRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    private ProductModel buildProductModel() {
        ProductModel productModel = new ProductModel();
        productModel.setName("Processador AMD Ryzen 5");
        productModel.setPrice(new BigDecimal("990"));
        return productModel;
    }

    @Test
    @DisplayName("Deve criar e retornar o produto corretamente")
    void deveCriarProduto() {

        var dto = new ProductCreateDto("Processador AMD Ryzen 5", new BigDecimal("990"));

        var product = buildProductModel();

        var response = new ProductResponseDto(product.getId(),"Processador AMD Ryzen 5", new BigDecimal("990"));

        Mockito.when(productMapper.toModel(dto)).thenReturn(product);

        Mockito.when(productsRepository.save(product)).thenReturn(product);

        Mockito.when(productMapper.toDto(product)).thenReturn(response);

        var result = productService.create(dto);

        assertEquals(response, result);
    }

    @Test
    @DisplayName("Deve retornar o produto quando o id existir")
    void deveRetornarProdutoSeIdExistir() {
        var id = UUID.randomUUID();

        var product = buildProductModel();
        product.setId(id);

        var response = new ProductResponseDto(id,"Processador AMD Ryzen 5", new BigDecimal("990"));

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.of(product));

        Mockito.when(productMapper.toDto(product)).thenReturn(response);

        var result = productService.findById(id);

        assertEquals(response, result);

        verify(productsRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção caso o Id for inexistente")
    void deveLancarExcecaoCasoIdInexistente() {
        var id = UUID.randomUUID();

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(id));

        verify(productsRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando o nome for nulo")
    void deveRetornarPaginaVaziaQuandoNulo(){
        Pageable pageable = PageRequest.of(0, 10);

        var result = productService.searchByName(null, pageable);

        assertTrue(result.data().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando o nome for vazio")
    void deveRetornarPaginaVaziaQuandoVazio(){
        Pageable pageable = PageRequest.of(0, 10);

        var result = productService.searchByName("", pageable);

        assertTrue(result.data().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar produtos quando o nome for válido")
    void deveRetornarProdutosQuandoNomeValido() {
        String name = "Processador";
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();
        var product = buildProductModel();
        product.setId(id);

        var responseDto = new ProductResponseDto(id, product.getName(), product.getPrice());

        Page<ProductModel> productPage = new PageImpl<>(List.of(product));
        Page<ProductResponseDto> responsePage = new PageImpl<>(List.of(responseDto));

        Mockito.when(productsRepository.findByNameContainingIgnoreCase(name, pageable))
                .thenReturn(productPage);

        Mockito.when(productMapper.toDto(product))
                .thenReturn(responseDto);

        var result = productService.searchByName(name, pageable);

        assertEquals(responsePage.getContent(), result.data());
    }

    @Test
    @DisplayName("Deve retornar produtos quando a faixa de preço for válida")
    void deveRetornarProdutosQuandoFaixaValida(){
        BigDecimal minPrice = new BigDecimal("100");
        BigDecimal maxPrice = new BigDecimal("1000");
        Pageable pageable = PageRequest.of(0, 10);

        var id = UUID.randomUUID();
        var product = buildProductModel();
        product.setId(id);

        ProductResponseDto responseDto = new ProductResponseDto(id, product.getName(), product.getPrice());

        Page<ProductModel> productPage = new PageImpl<>(List.of(product));
        Page<ProductResponseDto> responsePage = new PageImpl<>(List.of(responseDto));

        Mockito.when(productsRepository.findByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(productPage);

        Mockito.when(productMapper.toDto(product)).thenReturn(responseDto);

        var result = productService.findByPriceBetween(minPrice, maxPrice, pageable);

        assertEquals(responsePage.getContent(), result.data());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando minPrice for nulo")
    void deveLancarExceptionQuandoMinPriceNulo() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class, () -> productService.findByPriceBetween(null, new BigDecimal("1000"), pageable));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando maxPrice for nulo")
    void deveLancarExceptionQuandoMaxPriceNulo() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class, () -> productService.findByPriceBetween(new BigDecimal("100"), null, pageable));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando minPrice for maior que maxPrice")
    void deveLancarExceptionQuandoMinPriceMaiorQueMaxPrice() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class, () -> productService.findByPriceBetween(new BigDecimal("100"), new BigDecimal("99"), pageable));
    }

    @Test
    @DisplayName("Deve retornar página com produtos corretamente")
    void deveRetornarPaginaComProdutos() {
        Pageable pageable = PageRequest.of(0, 10);

        var product = buildProductModel();
        var responseDto = new ProductResponseDto(product.getId(), product.getName(), product.getPrice());

        Page<ProductModel> productPage = new PageImpl<>(List.of(product));
        Page<ProductResponseDto> responsePage = new PageImpl<>(List.of(responseDto));

        Mockito.when(productsRepository.findAll(pageable)).thenReturn(productPage);

        Mockito.when(productMapper.toDto(product)).thenReturn(responseDto);

        var result = productService.findAll(pageable);

        assertEquals(responsePage.getContent(), result.data());
    }

    @Test
    @DisplayName("Deve atualizar e retornar o produto quando o id existir")
    void deveAtualizarProdutoSeIdExistir() {
        var id = UUID.randomUUID();
        var product = buildProductModel();
        product.setId(id);

        var updateDto = new ProductUpdateDto("Processador AMD Ryzen 7", new BigDecimal("1500"));
        var responseDto = new ProductResponseDto(id, product.getName(), product.getPrice());

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productMapper.toDto(product)).thenReturn(responseDto);

        var result = productService.update(id,updateDto);

        assertEquals(responseDto, result);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o id não existir ao tentar dar update")
    void deveLancarExceptionQuandoIdNaoExistirNoUpdate() {
        var id = UUID.randomUUID();

        var updateDto = new ProductUpdateDto("Processador AMD Ryzen 7", new BigDecimal("1500"));
        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(id, updateDto));

        verify(productsRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando name e price forem ambos nulos")
    void deveLancarExceptionQuandoNameAndPriceNulos() {
        var id = UUID.randomUUID();

        var product = buildProductModel();
        product.setId(id);

        var updateDto = new ProductUpdateDto(null, null);

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> productService.update(id, updateDto));
    }

    @Test
    @DisplayName("Deve deletar o produto quando o id existir")
    void deveDeletarProdutoSeIdExistir() {
        var id = UUID.randomUUID();
        var product = buildProductModel();
        product.setId(id);

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.of(product));

        productService.delete(id);

        verify(productsRepository, times(1)).findById(id);
        verify(productsRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o id não existir ao tentar deletar")
    void deveLancarExceptionQuandoIdNaoExistirNoDelete() {
        var id = UUID.randomUUID();

        Mockito.when(productsRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.delete(id));

        verify(productsRepository, times(1)).findById(id);
    }

}
