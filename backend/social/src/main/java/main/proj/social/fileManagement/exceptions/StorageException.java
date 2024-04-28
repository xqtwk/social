package main.proj.social.fileManagement.exceptions;

public class StorageException extends Throwable {
    public StorageException(String message) {
        super(message);
    }
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
