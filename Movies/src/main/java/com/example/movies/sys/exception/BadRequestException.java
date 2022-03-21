package com.example.movies.sys.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(){
        super(String.format("Invalid request."));
    }
}
