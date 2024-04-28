package main.proj.social.fileManagement;

import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.fileManagement.exceptions.StorageFileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {
    @Autowired
    private FileService fileService;

    @Test
    public void storeAndLoadAvatar(@TempDir Path tempDir) throws Exception, StorageException {
        fileService.setRootLocation(tempDir);

        MultipartFile multipartFile = new MockMultipartFile(
                "user1_avatar.jpg",
                "user1_avatar.jpg",
                "image/jpeg",
                "This is a test avatar".getBytes());

        fileService.storeAvatar(multipartFile, "user1");

        Path storedAvatar = tempDir.resolve("user1/user1_avatar.jpg");
        assertTrue(Files.exists(storedAvatar));

        Resource loadedAvatar = fileService.loadAsResource("user1_avatar.jpg", "user1");
        assertNotNull(loadedAvatar);
        assertTrue(loadedAvatar.isReadable());
    }

    @Test
    public void testStoreEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "empty.jpg",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        StorageException exception = assertThrows(StorageException.class, () -> {
            fileService.storeAvatar(emptyFile, "user1");
        });

        assertTrue(exception.getMessage().contains("Failed to store empty file"));
    }

    @Test
    public void testLoadPhoto(@TempDir Path tempDir) throws Exception, StorageException {
        fileService.setRootLocation(tempDir);

        MultipartFile mockFile = new MockMultipartFile(
                "testphoto.jpg", "testphoto.jpg", "image/jpeg", "This is a test image content".getBytes());

        String storedFilename = fileService.storePostPhoto(mockFile, "user1");

        Path storedPhotoPath = tempDir.resolve("user1").resolve(storedFilename);
        System.out.println("Stored photo path: " + storedPhotoPath);
        assertTrue(Files.exists(storedPhotoPath), "File should exist at the path: " + storedPhotoPath);

        Resource loadedPhoto = fileService.loadAsResource(storedFilename, "user1");

        assertNotNull(loadedPhoto, "Loaded photo should not be null");
        assertTrue(loadedPhoto.isReadable(), "Loaded photo should be readable");
        assertEquals(mockFile.getSize(), loadedPhoto.contentLength());
    }
}