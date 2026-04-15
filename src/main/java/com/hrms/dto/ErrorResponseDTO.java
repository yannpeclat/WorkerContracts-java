package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    
    private int status;
    private String code;
    private List<FieldErrorDTO> fieldErrors;
    
    public static ErrorResponseDTO of(int status, String code) {
        return new ErrorResponseDTO(status, code, null);
    }
    
    public static ErrorResponseDTO withFieldErrors(int status, String code, List<FieldErrorDTO> fieldErrors) {
        return new ErrorResponseDTO(status, code, fieldErrors);
    }
}
