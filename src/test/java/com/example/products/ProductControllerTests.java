package com.example.products;

import com.example.products.controllers.ProductController;
import com.example.products.dtos.PageResponseDto;
import com.example.products.dtos.ProductResponseDto;
import com.example.products.security.CustomUserDetailsService;
import com.example.products.security.JwtService;
import com.example.products.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private ProductResponseDto buildResponseDto(){
        return new ProductResponseDto(UUID.randomUUID(), "Processador AMD Ryzen 7", new BigDecimal("990.00"));

    }

    @Test
    @DisplayName("Deve acessar o endpoint POST e criar um produto corretamente")
    void deveCriarUmProduto() throws Exception {
        var responseDto = buildResponseDto();

        Mockito.when(productService.create(Mockito.any())).thenReturn(responseDto);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "Processador AMD Ryzen 5",
                    "price": 990.00
                }
            """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve acessar endpoint GET e retornar uma página com todos os produtos")
    void deveRetornarTodosProdutos() throws Exception{
        var responseDto = buildResponseDto();

        PageResponseDto<ProductResponseDto> pageResponse = new PageResponseDto<>(new PageImpl<>(List.of(responseDto)));

        Mockito.when(productService.findAll(Mockito.any())).thenReturn(pageResponse);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Deve acessar o endpoint GET e retornar o produto com o Id correspondente")
    void deveRetornarProdutoComId() throws Exception{
        var responseDto = buildResponseDto();

        Mockito.when(productService.findById(Mockito.any())).thenReturn(responseDto);

        mockMvc.perform(get("/products/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    @DisplayName("Deve acessar o endpoint GET e retornar uma página de produtos relacionados ao nome")
    void deveRetornarProdutoContendoONome() throws Exception{
        var responseDto = buildResponseDto();

        PageResponseDto<ProductResponseDto> pageResponse = new PageResponseDto<>(new PageImpl<>(List.of(responseDto)));

        Mockito.when(productService.searchByName(Mockito.eq("Processador"), Mockito.any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/products/search?name=Processador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Deve acessar o endpoint GET e retornar uma página com os produtos dentro da faixa de preço")
    void deveRetornarProdutosNaFaixaDePreco() throws Exception{
        var responseDto = buildResponseDto();

        PageResponseDto<ProductResponseDto> pageResponse = new PageResponseDto<>(new PageImpl<>(List.of(responseDto)));

        Mockito.when(productService.findByPriceBetween(Mockito.eq(new BigDecimal("600")),Mockito.eq(new BigDecimal("1000")), Mockito.any())).thenReturn(pageResponse);

        mockMvc.perform(get("/products/price?minPrice=600&maxPrice=1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Deve acessar o endpoint PATCH e alterar atributos do produto corretamente")
    void deveAlterarAtributosDoProduto() throws Exception{
        var responseDto = buildResponseDto();

        Mockito.when(productService.update(Mockito.any(), Mockito.any())).thenReturn(responseDto);

        mockMvc.perform(patch("/products/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "name": "Processador AMD Ryzen 5"
                            }"""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve acessar o endpoint DELETE e deletar o produto com o Id correspondente")
    void deveDeletarProdutoComId() throws Exception{
        var id = UUID.randomUUID();

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());
    }
}
