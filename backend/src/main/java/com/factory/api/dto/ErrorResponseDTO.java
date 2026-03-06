package com.factory.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ErrorResponseDTO {
    public String error;
    public int status;

    public ErrorResponseDTO(String error, int status) {
        this.error = error;
        this.status = status;
    }
}