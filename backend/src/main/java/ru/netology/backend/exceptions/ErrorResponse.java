package ru.netology.backend.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String error;

    private String description;

    public ErrorResponse(int status, String error, String description) {
        this.status = status;
        this.error = error;
        this.description = description;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

}
