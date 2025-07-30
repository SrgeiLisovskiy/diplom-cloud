package ru.netology.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.backend.dto.AuthRequest;
import ru.netology.backend.dto.AuthToken;
import ru.netology.backend.util.JwtTokenUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public String getToken(AuthRequest authRequest) {
        log.info("Выполнен запрос POST/cloud/login: {}", authRequest);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
            UserDetails userDetails = userService.loadUserByUsername(authRequest.getLogin());
            String token = jwtTokenUtil.generateToken(userDetails);
            if (token==null){
                log.error("Неверные учетные данные для пользователя: {}", authRequest.getLogin());
                throw new RuntimeException("Bad credentials");
            }
            return token;
    }

}
