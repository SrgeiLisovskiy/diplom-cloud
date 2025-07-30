package ru.netology.backend.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.netology.backend.model.JwtBlackList;
import ru.netology.backend.repository.JwtBlackListRepository;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final JwtBlackListRepository jwtBlackListRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Auth-Token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);

        if (token != null) {
            JwtBlackList jwtBlackList = new JwtBlackList();
            jwtBlackList.setToken(token);
            jwtBlackListRepository.save(jwtBlackList);
        }
    }
}
