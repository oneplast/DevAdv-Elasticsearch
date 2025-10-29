package io.eddie.core.model.web;

public record BaseResponse<T>(
        T data,
        String message
) {
}
