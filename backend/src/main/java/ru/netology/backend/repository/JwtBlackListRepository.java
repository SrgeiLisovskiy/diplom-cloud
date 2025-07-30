package ru.netology.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.backend.model.JwtBlackList;

import java.util.Optional;
@Repository
public interface JwtBlackListRepository extends JpaRepository<JwtBlackList, Long> {
    Optional<JwtBlackList> getJwtBlackListByToken(String token);
}
