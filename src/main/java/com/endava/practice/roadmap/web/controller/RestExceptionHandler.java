package com.endava.practice.roadmap.web.controller;

import com.endava.practice.roadmap.web.dto.ApiErrorDto;
import com.endava.practice.roadmap.web.exception.BadRequestException;
import com.endava.practice.roadmap.web.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler (value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(final ResourceNotFoundException ex, final WebRequest request) {
        return handleExceptionInternal(ex, apiMessage(NOT_FOUND, ex), new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler (value = BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(final BadRequestException ex, final WebRequest request) {
        return handleExceptionInternal(ex, apiMessage(BAD_REQUEST, ex), new HttpHeaders(), BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers,
                                                                  final HttpStatus status, final WebRequest request) {
        log.debug("Submitted a malformed json");
        return handleExceptionInternal(ex, apiMessage(BAD_REQUEST, ex), headers, BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers,
                                                                  final HttpStatus status, final WebRequest request) {
        log.debug("Submitted a JSON with unexpected property");
        return handleExceptionInternal(ex, apiMessage(BAD_REQUEST, ex), headers, BAD_REQUEST, request);
    }

    private ApiErrorDto apiMessage(final HttpStatus httpStatus, final Exception ex) {
        final String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        final String devMessage = ExceptionUtils.getRootCauseMessage(ex);
        return new ApiErrorDto(httpStatus.toString(), message, devMessage);
    }
}
