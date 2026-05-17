package com.example.products.services;

import com.example.products.dtos.LoginDto;
import com.example.products.dtos.RegisterDto;
import com.example.products.exceptions.BadRequestException;
import com.example.products.exceptions.PasswordMismatchException;
import com.example.products.exceptions.UserAlreadyExistsException;
import com.example.products.models.Role;
import com.example.products.models.User;
import com.example.products.repositories.UserRepository;
import com.example.products.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(LoginDto dto) {
        log.info("Executando login do usuário");

        String email = dto.email().toLowerCase().trim();

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        dto.password()
                )
        );

        User userModel = (User) auth.getPrincipal();

        log.info("Usuário encontrado com sucesso");

        return jwtService.generateToken(userModel);
    }

    public String registerAdmin(RegisterDto dto) {
        return createUser(dto,Role.ROLE_ADMIN);
    }

    public String registerUser(RegisterDto dto) {
        return createUser(dto,Role.ROLE_USER);
    }

    public String createUser(RegisterDto dto, Role role) {
        log.info("Registrando usuário");

        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("Email é obrigatório");
        }

        if (dto.password() == null || dto.confirmPassword() == null ||
                !dto.password().equals(dto.confirmPassword())) {
            throw new PasswordMismatchException("Senhas não conferem");
        }

        String email = dto.email().toLowerCase().trim();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe com email: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(role);

        userRepository.save(user);

        log.info("Usuário registrado com sucesso. email={}", email);

        return jwtService.generateToken(user);
    }
}
