package com.axislab.crmmedico.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " não encontrado(a) com ID: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
