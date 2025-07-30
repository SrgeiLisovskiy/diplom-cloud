package ru.netology.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.backend.model.FileSaved;
import ru.netology.backend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileSaved, Long> {
    List<FileSaved> findAllByUser(User user);

    Optional<FileSaved> findByUserAndFilename(User user, String filename);

    void deleteByFilenameAndUser(String filename, User user);
}
