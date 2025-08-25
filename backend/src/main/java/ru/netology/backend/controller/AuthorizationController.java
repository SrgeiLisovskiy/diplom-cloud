package ru.netology.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.backend.dto.AuthRequest;
import ru.netology.backend.service.AuthorizationService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        log.info("Поптыка входа пользователя:{}", authRequest);
        return authorizationService.getToken(authRequest);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Auth-Token") String authHeader){
        log.info("Пользователь с токеном {} вышел из сети", authHeader);
        return ResponseEntity.ok("Success logout");
    }
}
