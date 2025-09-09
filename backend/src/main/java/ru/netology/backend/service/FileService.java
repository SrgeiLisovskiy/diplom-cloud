package ru.netology.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exceptions.NotFoundException;
import ru.netology.backend.exceptions.UnauthorizedException;
import ru.netology.backend.model.FileSaved;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.FileRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;


    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public void uploadFile(User user, String fileName, MultipartFile file)  {
        Path path = Paths.get(uploadDir, fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            log.debug("Файл успешно сохранен: {}", fileName);
        } catch (IOException e) {
            log.debug("Ошибка сохранение файла: {}", fileName);
            throw new NotFoundException("Ошибка сохранение файла: " + fileName);
        }

        FileSaved fileSaved = new FileSaved();
        fileSaved.setFilename(fileName);
        fileSaved.setSize(file.getSize());
        fileSaved.setUser(user);
        fileRepository.save(fileSaved);
    }

    public FileSaved getFile(User user, String filename)  {
        return fileRepository.findByUserAndFilename(user, filename).orElse(null);
    }

    public List<FileSaved> getFiles(User user, Integer limit) {
        List<FileSaved> listFiles = fileRepository.findAllByUser(user);
        return limit == null ? listFiles : listFiles.stream().limit(limit).collect(Collectors.toList());
    }

    @Transactional
    public void deleteFile(User user, String filename) {
        fileRepository.deleteByFilenameAndUser(filename, user);
        Path path = Paths.get(uploadDir, filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.debug("Ошибка удаления файла: {}", filename);
            throw new NotFoundException("Ошибка удаления файла: " + e );
        }
    }

    @Transactional
    public void renameFile(User user, String filename, Map<String, String> body) throws IOException {
        String newName = body.get("filename");

        Path oldPath = Paths.get(uploadDir, filename);
        Path newPath = Paths.get(uploadDir, newName);

        FileSaved oldFile = fileRepository.findByUserAndFilename(user, filename).orElse(null);

        if (oldFile == null) {
            throw new FileNotFoundException("Оригинальный файл с именем " + filename + " не найден");
        }
        Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        oldFile.setFilename(newName);
        fileRepository.save(oldFile);

        log.info("Пользователь: {} переименовывает файл: {} → {}", user.getLogin(), filename, newName);
    }


}
