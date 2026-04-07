package com.moviebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    private Boolean success;
    private String message;
    private Object data;

    public static ApiResponseDto success(String message, Object data) {
        return new ApiResponseDto(true, message, data);
    }

    public static ApiResponseDto success(String message) {
        return new ApiResponseDto(true, message, null);
    }

    public static ApiResponseDto error(String message) {
        return new ApiResponseDto(false, message, null);
    }
}
