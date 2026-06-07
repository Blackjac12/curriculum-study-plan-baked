package com.example.Integration.project.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}