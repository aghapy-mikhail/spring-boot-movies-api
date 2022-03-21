package com.example.movies.sys.controller;


import com.example.movies.sys.entity.Error;
import com.example.movies.sys.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;


@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    private Error createError(Exception e){

        return new Error(new Date(), e.getClass().getSimpleName(), e.getMessage());

    }

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<Object>(createError(e), HttpStatus.BAD_REQUEST);
    }

    @Override
    public final ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return new ResponseEntity<Object>(createError(e), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<Object>(createError(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<Object> handleAllExceptions(InternalServerException e) {
        return new ResponseEntity<Object>(createError(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExistingRecordException.class)
    public final ResponseEntity<Object> handleExistingRecordException(ExistingRecordException e){
        return new ResponseEntity<Object>(createError(e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotExistingGenre.class)
    public final ResponseEntity<Object> handleNotExistingGenreException(NotExistingGenre e){
        Error error=new Error(new Date(), e.getClass().getSimpleName(),e.getMessage());
        return new ResponseEntity<Object>(error, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(BadRequestException.class)
//    public final ResponseEntity<Object> handleBadRequestException(BadRequestException e){
//        Error error=new Error(new Date(), e.getClass().getSimpleName(),e.getMessage());
//        return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
//    }


}
