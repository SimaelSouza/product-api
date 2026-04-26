package com.example.products;

import com.example.products.dtos.LoginDto;
import com.example.products.dtos.RegisterDto;
import com.example.products.models.Role;
import com.example.products.models.User;
import com.example.products.repositories.UserRepository;
import com.example.products.security.JwtService;
import com.example.products.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User buildUser(){
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        return user;
    }

    @Test
    @DisplayName("Deve retornar token quando credenciais forem válidas no Login")
    void deveRetornarTokenQuandoLoginValido(){
        var user = buildUser();

        var loginDto = new LoginDto(user.getEmail(), user.getPassword());

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(user);

        Mockito.when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("email@email.com", "password")
        )).thenReturn(auth);

        Mockito.when(jwtService.generateToken(user)).thenReturn("token");

        var result = authService.login(loginDto);

        assertEquals("token", result);
    }

    @Test
    @DisplayName("Deve registrar e retornar token quando os dados forem válidos com ROLE_USER")
    void deveRegistrarSeDadosValidosUser(){
        var registerDto = new RegisterDto("simael@gmail.com", "123456", "123456");

        var user = new User(registerDto.email(), registerDto.password());
        var role = Role.ROLE_USER;
        user.setRole(role);

        Mockito.when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode(registerDto.password())).thenReturn("password");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(jwtService.generateToken(Mockito.any(User.class))).thenReturn("token");

        var result = authService.createUser(registerDto, role);

        assertEquals("token", result);
    }

    @Test
    @DisplayName("Deve registrar e retornar token quando os dados forem válidos com ROLE_ADMIN")
    void deveRegistrarSeDadosValidosAdmin(){
        var registerDto = new RegisterDto("simael@gmail.com", "123456", "123456");

        var user = new User(registerDto.email(), registerDto.password());
        var role = Role.ROLE_ADMIN;
        user.setRole(role);

        Mockito.when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode(registerDto.password())).thenReturn("password");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(jwtService.generateToken(Mockito.any(User.class))).thenReturn("token");

        var result = authService.createUser(registerDto, role);

        assertEquals("token", result);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando as senhas não conferirem")
    void deveLancarExceptionQuandoSenhasDiferentes(){
        var registerDto = new RegisterDto("simael@gmail.com", "123456", "Diferente");
        var role = Role.ROLE_USER;

        assertThrows(IllegalArgumentException.class, () -> authService.createUser(registerDto, role));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o email já estiver cadastrado")
    void deveLancarExceptionQuandoEmailCadastrado(){
        var registerDto = new RegisterDto("email@email.com", "123456", "123456");
        var role = Role.ROLE_USER;

        var user = buildUser();
        user.setRole(role);

        Mockito.when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.createUser(registerDto, role));
    }
}
