package com.example.ImageEditor.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

@Controller
public class ImageController {

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            String contentType = file.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                model.addAttribute("message", "Ошибка: поддерживаются только форматы JPEG и PNG");
                return "uploadStatus";
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get("src/main/resources/static/uploads/" + file.getOriginalFilename());
            Files.write(path, bytes);
            model.addAttribute("message", "Файл успешно загружен: " + file.getOriginalFilename());
            model.addAttribute("imagePath", "/uploads/" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Ошибка загрузки файла: " + e.getMessage());
        }
        return "uploadStatus";
    }

    @PostMapping("/saveImage")
    @ResponseBody
    public ResponseEntity<String> saveImage(@RequestBody Map<String, String> payload) {
        try {
            String base64Image = payload.get("image").split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Path path = Paths.get("src/main/resources/static/uploads/resized_image.jpg");
            Files.write(path, imageBytes);
            return ResponseEntity.ok("Изображение сохранено");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сохранения изображения");
        }
    }
}
