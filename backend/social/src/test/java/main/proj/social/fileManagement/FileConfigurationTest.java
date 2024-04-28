package main.proj.social.fileManagement;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"storage.location=./storage"})
class FileConfigurationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    public void rootLocationBeanTest() {
        Path rootLocation = context.getBean(Path.class);
        assertNotNull(rootLocation);
        assertTrue(rootLocation.toString().contains("storage"));
    }
}