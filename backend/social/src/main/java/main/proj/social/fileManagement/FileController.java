package main.proj.social.fileManagement;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.proj.social.fileManagement.exceptions.StorageException;
import main.proj.social.fileManagement.exceptions.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/files")
@Tag(name = "Files")
public class FileController {
    private final FileService fileService;

   /*@PostMapping("/photos")
    public ResponseEntity<String> uploadPhotos(@RequestParam("files") List<MultipartFile> files, Principal principal) {
        if (files.size() > 5) {
            return ResponseEntity.badRequest().body("Cannot upload more than 5 photos");
        }

        for (MultipartFile file : files) {
            try {
                fileService.storePostPhoto(file, principal.getName());
            } catch (StorageException e) {
                return ResponseEntity.badRequest().body("Failed to upload photo: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Photos uploaded successfully");
    }*/

    @GetMapping("/{username}/{filename}")
    public ResponseEntity<Resource> displayFile(@PathVariable String username, @PathVariable String filename) throws StorageFileNotFoundException {
        Resource file = fileService.loadAsResource(filename, username);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
