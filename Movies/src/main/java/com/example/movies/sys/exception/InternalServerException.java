package com.example.movies.sys.exception;

public class InternalServerException extends RuntimeException{
    public  InternalServerException(String mess){
        super(String.format("There is an issue regarding the service: %s",mess));
    }
}
