package ru.netology.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void loadUserByUsernameTest() {
        User user = new User();
        String password = passwordEncoder.encode("test");
        String login = "test";
        user.setLogin(login);
        user.setPassword(password);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        assertEquals(user.getLogin(), userService.loadUserByUsername("test").getUsername());
    }
}