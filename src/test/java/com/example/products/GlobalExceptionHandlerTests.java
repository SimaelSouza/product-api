package com.example.products;

import com.example.products.exceptions.GlobalExceptionHandler;
import com.example.products.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTests {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 404 quando lançar ResourceNotFoundException")
    void deveRetornarNotFoundQuandoResourceNotFound() {
        var resourceNotFoundException = new ResourceNotFoundException("Produto não encontrado");
        var request = new MockHttpServletRequest();
        request.setRequestURI("api/products/123");

        var response = handler.handleResourceNotFound(resourceNotFoundException, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Produto não encontrado", response.getBody().message());
        assertEquals("api/products/123", response.getBody().path());
    }
}
