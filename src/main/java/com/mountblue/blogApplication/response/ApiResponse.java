package com.mountblue.blogApplication.response;

public record ApiResponse<T>(boolean success,
                             String message,
                             T data) {
}
