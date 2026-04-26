package com.example.products;

import com.example.products.models.Role;
import com.example.products.models.User;
import com.example.products.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User buildUser(){
        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        return user;
    }

    @Test
    @DisplayName("Deve retornar o usuário quando o email existir")
    void deveRetornarUsuarioSeEmailExistir(){
        User user = buildUser();
        entityManager.persist(user);

        entityManager.flush();

        var result = userRepository.findByEmail(user.getEmail());

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve retornar Optional.empty() quando o email não existir")
    void deveRetornarEmptySeEmailNaoExistir(){
        var result = userRepository.findByEmail("email@email.com");

        assertTrue(result.isEmpty());
    }

}
