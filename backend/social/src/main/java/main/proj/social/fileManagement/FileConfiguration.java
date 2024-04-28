package main.proj.social.fileManagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileConfiguration {

    @Value("${storage.location}")
    private String storageLocation;

    @Bean
    public Path rootLocation() {
        return Paths.get(storageLocation).toAbsolutePath().normalize();
    }

}
