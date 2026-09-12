package it.uniroma3.siw.festivalcinema.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.festivalcinema.exception.InvalidPosterException;


//salva foto locandine nella cartella scelta da app.upload-dir
//vengono caricati da spring e webconfiguration

@Service
public class PosterStorageService {

    private final Path uploadDir;

    public PosterStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir);
    }

    public String save(MultipartFile file) throws InvalidPosterException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidPosterException("La locandina deve essere un'immagine (jpg, png, webp...)");
        }
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        }
        //uuid x no collisioni
        String fileName = "festival-" + UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(fileName));
        } catch (IOException e) {
            throw new InvalidPosterException("Impossibile salvare la locandina: " + e.getMessage());
        }
        return fileName;
    }

    public void delete(String fileName) {
        if (fileName == null) {
            return;
        }
        try {
            Files.deleteIfExists(uploadDir.resolve(fileName));
        } catch (IOException e) {
        }
    }
}
