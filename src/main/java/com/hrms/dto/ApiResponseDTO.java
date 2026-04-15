package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private ErrorResponseDTO error;
    
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return new ApiResponseDTO<>(true, message, data, LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponseDTO<T> error(String message, ErrorResponseDTO error) {
        return new ApiResponseDTO<>(false, message, null, LocalDateTime.now(), error);
    }
}
