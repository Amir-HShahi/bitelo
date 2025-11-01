package dev.burgerman.bitelo.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.burgerman.bitelo.model.dto.ErrorResponse;
import dev.burgerman.bitelo.model.exception.*;

import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
     * --------------------------------------------------------------------- *
     * 4xx CLIENT ERRORS
     * ---------------------------------------------------------------------
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnverifiedUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleUnverifiedUser(UnverifiedUserException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidToken(UnverifiedUserException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleUserNotFound(UnverifiedUserException ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req, HttpStatus.NOT_FOUND);
    }

    /*
     * --------------------------------------------------------------------- *
     * 5xx SERVER ERRORS (fallback)
     * ---------------------------------------------------------------------
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest req) {
        log.error("Unexpected exception (traceId={})", getTraceId(req), ex);
        return buildErrorResponse(
                ex,
                req,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred. Please try again later.");
    }

    private ErrorResponse buildErrorResponse(
            Throwable ex,
            HttpServletRequest req,
            HttpStatus status) {
        return buildErrorResponse(ex, req, status, ex.getMessage());
    }

    private ErrorResponse buildErrorResponse(
            Throwable ex,
            HttpServletRequest req,
            HttpStatus status,
            String message) {

        String traceId = getTraceId(req);
        logClientError(status, ex, traceId);
        return new ErrorResponse(
                message,
                req.getRequestURI(),
                traceId);
    }

    private String getTraceId(HttpServletRequest req) {
        String header = req.getHeader("X-Trace-Id");
        if (header != null && !header.isBlank()) {
            return header;
        }
        return UUID.randomUUID().toString();
    }

    private void logClientError(HttpStatus status, Throwable ex, String traceId) {
        if (status.is5xxServerError()) {
            log.error("Server error (traceId={}): {}", traceId, ex.toString(), ex);
        } else {
            log.warn("{} (traceId={}): {}", status.value(), traceId, ex.getMessage());
        }
    }
}