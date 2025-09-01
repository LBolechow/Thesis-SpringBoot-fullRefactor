package pl.lukbol.dyplom.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.lukbol.dyplom.DTOs.exception.ErrorMessageDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static String error_msg = "Error message: ";

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<ErrorMessageDTO> handleAuthExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessageDTO(error_msg, ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessageDTO> handleDatabaseException(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(error_msg, "Database error: " + ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.UserWithEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserAlreadyExists(ApplicationException.UserWithEmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(error_msg, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.PasswordsMismatchException.class)
    public ResponseEntity<ErrorMessageDTO> handlePasswordsMismatch(ApplicationException.PasswordsMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(error_msg, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.UserNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleUserNotFound(ApplicationException.UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.ParticipantsListIsEmptyException.class)
    public ResponseEntity<ErrorMessageDTO> handleParticipantsEmpty(ApplicationException.ParticipantsListIsEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(error_msg, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> ConversationNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, "Conversation error: " + ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> LastMessageNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, "Message error: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> InvalidDateException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, "Invalid date error: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> OrderNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, "Order error: " + ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> MaterialNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(error_msg, "Material error: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleAllOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO(error_msg, "Unexpected error: " + ex.getMessage()));
    }


}
