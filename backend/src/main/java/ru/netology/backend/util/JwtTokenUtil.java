package ru.netology.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.backend.exceptions.UnauthorizedException;
import ru.netology.backend.model.JwtBlackList;
import ru.netology.backend.repository.JwtBlackListRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifeTime;

    private final JwtBlackListRepository jwtBlackListRepository;

    public String generateToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + lifeTime.toMillis());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + lifeTime.toMillis()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getLogin(String token) throws UnauthorizedException {
        return getClaimsFromToken(token).getSubject();

    }

    public Claims getClaimsFromToken(String token) throws UnauthorizedException {
        Optional<JwtBlackList> jwtBlackList = jwtBlackListRepository.getJwtBlackListByToken(token);
        if (jwtBlackList.isPresent()) {
            log.debug("Ошибка авторизации: токен не действителен");
            throw new UnauthorizedException("Ошибка авторизации: токен не действителен");
        }
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
