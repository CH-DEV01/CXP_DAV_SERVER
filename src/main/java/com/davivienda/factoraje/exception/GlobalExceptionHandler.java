package com.davivienda.factoraje.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja la excepción de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> manejarResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // 404 Not Found
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        ErrorDetails errorDetalles = new ErrorDetails(
            new Date(),
            ex.getMessage(),
            "Fallo en la autenticación"
        );
        return new ResponseEntity<>(errorDetalles, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    // Maneja la excepción de un recurso que ya existe
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // 409
    }

    // Maneja la excepción de un caso de negocio no válido
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorDetails> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // 400
    }

    // Maneja la excepción de un usuario con permisos insuficientes
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN); // 403
    }

    // Maneja la excepción cuando un servicio externo no esta disponible
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorDetails> handleExternalServiceException(ExternalServiceException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE); // 503
    }

    // Maneja cualquier otra excepción no controlada para evitar que el servidor se caiga
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> manejarExcepcionesGlobales(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}