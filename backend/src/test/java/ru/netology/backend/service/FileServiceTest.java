package ru.netology.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.backend.exceptions.UnauthorizedException;
import ru.netology.backend.model.FileSaved;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.FileRepository;
import ru.netology.backend.util.JwtTokenUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;
    @TempDir
    private Path tempDir;

    private final User user = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // вручную устанавливаем путь загрузки, так как @Value не срабатывает в юнит-тесте
        try {
            Field field = fileService.getClass().getDeclaredField("uploadDir");
            field.setAccessible(true);
            field.set(fileService, tempDir.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testUploadFile() {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        fileService.uploadFile(user, "test.txt", file);
        assertTrue(Files.exists(tempDir.resolve("test.txt")));
        verify(fileRepository, times(1)).save(any(FileSaved.class));
    }

    @Test
    void testGetFile() {
        FileSaved fileSaved = new FileSaved();
        fileSaved.setFilename("test.txt");
        when(fileRepository.findByUserAndFilename(user, "test.txt")).thenReturn(Optional.of(fileSaved));
        FileSaved fileTest = fileRepository.findByUserAndFilename(user, "test.txt").get();
        assertEquals(fileTest, fileSaved);
    }

    @Test
    void testGetFiles()  {
        when(fileRepository.findAllByUser(user)).thenReturn(List.of(new FileSaved()));
        List<FileSaved> result = fileService.getFiles(user, 1);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteFile() throws IOException{
        Path filePath = tempDir.resolve("test.txt");
        Files.write(filePath, "testText".getBytes());

        fileService.deleteFile(user, "test.txt");

        assertFalse(Files.exists(filePath));
        verify(fileRepository).deleteByFilenameAndUser("test.txt", user);
    }

}