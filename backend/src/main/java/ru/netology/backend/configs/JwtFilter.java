package ru.netology.backend.configs;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.backend.exceptions.UnauthorizedException;
import ru.netology.backend.service.CustomUserDetailsService;
import ru.netology.backend.util.JwtTokenUtil;

import java.io.IOException;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private final CustomUserDetailsService detailsService;

    public JwtFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService detailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.detailsService = detailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/login".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Auth-Token");
        log.info("Входящий заголовок авторизации: {}", authHeader);
        String jwtToken = null;
        String login = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                login = jwtTokenUtil.getLogin(jwtToken);
            } catch (ExpiredJwtException e) {
                log.debug("Время жизни токена истекло: {}", jwtToken);
            } catch (SignatureException e) {
                log.debug("Подпись не верна: {}", jwtToken);
            } catch (UnauthorizedException e) {
                log.debug("Не синхроннизированный пользователь");
            }
        }
        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = detailsService.loadUserByUsername(login);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
