package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private String field;
    private String resourceName;
    private String filedName;
    Long fieldId;

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        //calls the RuntimeException(String message) constructor.
        super(String.format("%s not found with %s : %d", resourceName, field, fieldId));
        this.field = field;
        this.resourceName = resourceName;
        this.filedName = filedName;
    }

    public ResourceNotFoundException() {
    }
}
