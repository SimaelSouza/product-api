package com.example.products;

import com.example.products.controllers.AuthController;
import com.example.products.security.CustomUserDetailsService;
import com.example.products.security.JwtService;
import com.example.products.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class AuthControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Deve acessar o endpoint POST e registrar user corretamente")
    void deveRegistrarUser() throws Exception {
        Mockito.when(authService.registerUser(Mockito.any())).thenReturn("token");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "user@email.com",
                                "password": "123456789",
                                "confirmPassword": "123456789"
                            }
                         """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Deve acessar o endpoint POST e registrar admin corretamente")
    void deveRegistrarAdmin() throws Exception {
        Mockito.when(authService.registerAdmin(Mockito.any())).thenReturn("token");

        mockMvc.perform(post("/auth/register-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "admin@email.com",
                                "password": "123456789",
                                "confirmPassword": "123456789"
                            }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Deve acessar o endpoint POST e logar usuário corretamente")
    void deveLogarUsuario() throws Exception {
        Mockito.when(authService.login(Mockito.any())).thenReturn("token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                             {
                                  "email": "email@email.com",
                                  "password": "123456789"
                             }
                         """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}
