package ru.netology.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        log.debug("Найден пользователь с login: {}, пароль из БД: {}", login, user.getPassword());

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getLogin());
        builder.password(user.getPassword());
        builder.roles("USER");

        return builder.build();
    }
}
