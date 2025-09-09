package ru.netology.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.backend.dto.AuthRequest;
import ru.netology.backend.dto.AuthToken;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;
import ru.netology.backend.util.JwtTokenUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthorizationService authorizationService;
    @Mock
    private AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testLoginOk()  {
        User user = new User();
        String password = passwordEncoder.encode("test");
        String login = "test";
        user.setLogin(login);
        user.setPassword(password);


        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(login);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("token");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, "test"));

        authorizationService = new AuthorizationService(userService, jwtTokenUtil, authenticationManager);

        ResponseEntity<?> authResponse = authorizationService.getToken(new AuthRequest(login, "test"));
        AuthToken authToken = (AuthToken) authResponse.getBody();

        assertEquals("token", authToken.getToken());

    }

    @Test
    void testLoginBadCredentials() {
        String login = "test";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, "test"));

        authorizationService = new AuthorizationService(userService, jwtTokenUtil, authenticationManager);

        assertEquals(HttpStatus.BAD_REQUEST, authorizationService.getToken(new AuthRequest(login, "test")).getStatusCode());

    }


}