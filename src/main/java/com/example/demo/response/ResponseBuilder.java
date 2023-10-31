package com.example.demo.response;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ApiResponse<T> buildSuccessResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> buildErrorResponse(int status, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
