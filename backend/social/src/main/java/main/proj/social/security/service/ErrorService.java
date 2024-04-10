package main.proj.social.security.service;

import main.proj.social.security.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;

public class ErrorService {
    public static ErrorResponse extractDataIntegrityViolationExceptionErrorMessage(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        if (message.contains("username")) {
            return new ErrorResponse("Username already exists");
        } else if (message.contains("email")) {
            return new ErrorResponse("Email already in use");
        } else {
            // Handle other potential constraints or provide a generic fallback
            return new ErrorResponse("A database constraint violation occurred: " + message);
        }
    }
}
