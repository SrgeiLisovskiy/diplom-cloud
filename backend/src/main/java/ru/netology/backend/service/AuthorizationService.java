package ru.netology.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.backend.dto.AuthRequest;
import ru.netology.backend.dto.AuthToken;
import ru.netology.backend.exceptions.NotFoundException;
import ru.netology.backend.util.JwtTokenUtil;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> getToken(AuthRequest authRequest) {
        log.info("Выполнен запрос POST/cloud/login: {}", authRequest);
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));

            UserDetails userDetails = userService.loadUserByUsername(authRequest.getLogin());
            String token = jwtTokenUtil.generateToken(userDetails);
            if (token==null){
                log.error("Неверные учетные данные для пользователя: {}", authRequest.getLogin());
                throw new BadCredentialsException("Bad credentials");
            }
            return ResponseEntity.ok(new AuthToken(token));

        } catch (BadCredentialsException e) {
            log.warn("Неверные учетные данные для пользователя: {}", authRequest.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        } catch (AuthenticationException e) {
            log.warn("Ошибка аутентификации для пользователя {}: {}", authRequest.getLogin(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            log.error("Исключение при попытке логина", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal error", "id", 500));
        }
    }

}
