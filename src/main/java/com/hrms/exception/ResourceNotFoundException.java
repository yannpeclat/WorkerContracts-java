--- src/main/java/com/hrms/exception/ResourceNotFoundException.java (原始)


+++ src/main/java/com/hrms/exception/ResourceNotFoundException.java (修改后)
package com.hrms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}