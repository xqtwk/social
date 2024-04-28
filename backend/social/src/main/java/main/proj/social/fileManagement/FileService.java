package main.proj.social.fileManagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.fileManagement.exceptions.StorageFileNotFoundException;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Setter
@Getter
public class FileService {
    private Path rootLocation;

    public void storeAvatar(MultipartFile file, String username) throws StorageException {
        validateFile(file);
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String avatarFilename = username + "_avatar" + extension;

        Path userDirectory = prepareUserDirectory(username);
        store(file, avatarFilename, userDirectory);
    }

    public String storePostPhoto(MultipartFile file, String username) throws StorageException {
        System.out.println("storepostphoto invoked");
        System.out.println("File is empty: " + file.isEmpty() + ", Size: " + file.getSize());
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }
        validateFile(file);
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String filename = username + "_" + UUID.randomUUID().toString() + extension;

        Path userDirectory = prepareUserDirectory(username);
        store(file, filename, userDirectory);
        return filename;
    }

    private void validateFile(MultipartFile file) throws StorageException {
        System.out.println("File is empty: " + file.isEmpty() + ", File size: " + file.getSize());

        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new StorageException("Only image files are allowed.");
        }
    }

    private Path prepareUserDirectory(String username) throws StorageException {
        Path userDirectory = rootLocation.resolve(username).normalize();
        try {
            Files.createDirectories(userDirectory);
        } catch (IOException e) {
            throw new StorageException("Could not create user directory.", e);
        }
        return userDirectory;
    }

    public void store(MultipartFile file, String filename, Path directory) throws StorageException {
        try {
            Path destinationFile = directory.resolve(filename).normalize();
            if (Files.exists(destinationFile)) {
                throw new StorageException("File already exists.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    private String getExtension(String filename) {
        return filename.lastIndexOf(".") != -1 ? filename.substring(filename.lastIndexOf(".")) : "";
    }

    public Resource loadAsResource(String filename, String username) throws StorageFileNotFoundException {
        try {
            Path userDirectory = rootLocation.resolve(username).normalize();
            Path filePath = userDirectory.resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
}
