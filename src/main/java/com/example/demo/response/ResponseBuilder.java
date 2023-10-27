package com.example.demo.response;

public class ResponseBuilder {

    public static <T> ApiResponse<T> buildSuccessResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static ApiResponse<?> buildErrorResponse(int status, String message) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }
}
