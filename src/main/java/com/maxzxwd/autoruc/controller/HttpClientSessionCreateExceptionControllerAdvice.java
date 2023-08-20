package com.maxzxwd.autoruc.controller;

import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class HttpClientSessionCreateExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpClientSessionFactory.HttpClientSessionCreateException.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(HttpClientSessionFactory.HttpClientSessionCreateException ex, WebRequest request) {

        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatusCode.valueOf(500), ex.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
