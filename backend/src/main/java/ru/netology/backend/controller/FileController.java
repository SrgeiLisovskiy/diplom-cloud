package ru.netology.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.backend.exceptions.UnauthorizedException;
import ru.netology.backend.model.FileSaved;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.UserRepository;
import ru.netology.backend.service.FileService;
import ru.netology.backend.util.JwtTokenUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class FileController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestHeader("Auth-Token") String authHeader,
            @RequestParam("filename") String fileName,
            @RequestPart MultipartFile file) throws UnauthorizedException {
        User user = getUser(authHeader);
        log.info("Загрузка файла: {}", file);
        fileService.uploadFile(user, fileName, file);
        return ResponseEntity.ok("Success upload");
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestHeader("Auth-Token") String authHeader,
                                     @RequestParam("filename") String filename) throws UnauthorizedException {
        User user = getUser(authHeader);
        log.info("Получение файла: {}", filename);
        return ResponseEntity.ok(fileService.getFile(user, filename));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getFiles(@RequestHeader("Auth-Token") String authHeader,
                                      @RequestParam(name = "limit", required = false) Integer limit) throws UnauthorizedException {
        log.info("Получение списка файлов с лимитом {}: ", limit);
        User user = getUser(authHeader);
        List<FileSaved> listFile = fileService.getFiles(user, limit);
        return ResponseEntity.ok(listFile);
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestHeader("Auth-Token") String authHeader,
                                        @RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> body) throws IOException, UnauthorizedException {
        User user = getUser(authHeader);
        log.info("Пользователь {} пытается переименовать фаил с именем: {}", user, filename);
        fileService.renameFile(user, filename, body);

        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("Auth-Token") String authHeader,
                                        @RequestParam("filename") String filename) throws UnauthorizedException {
        User user = getUser(authHeader);
        log.info("Пользователь {} удаляет файл: {}", user, filename);
        fileService.deleteFile(user, filename);
        return ResponseEntity.ok("Success deleted");
    }

    private User getUser(String authHeader) throws UnauthorizedException {
        String token = authHeader.replace("Bearer ", "");
        return getUserByToken(token);
    }

    private User getUserByToken(String token) throws UnauthorizedException {
        String login = jwtTokenUtil.getLogin(token);
        return userRepository.findByLogin(login).orElseThrow();
    }
}
