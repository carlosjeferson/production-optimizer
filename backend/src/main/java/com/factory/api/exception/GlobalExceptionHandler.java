package com.factory.api.exception;

import com.factory.api.dto.ErrorResponseDTO;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof ConstraintViolationException e) {
            String details = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(details, 400))
                    .build();
        }

        if (exception instanceof WebApplicationException e) {
            int status = e.getResponse().getStatus();
            return Response.status(status)
                    .entity(new ErrorResponseDTO(e.getMessage(), status))
                    .build();
        }

        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException hce) {
                String sqlState = hce.getSQLState();

                if ("23505".equals(sqlState)) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity(new ErrorResponseDTO("Produto com esse código já existe.", 409))
                            .build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponseDTO("Violação de integridade no banco de dados.", 400))
                            .build();
                }
            }
            cause = cause.getCause();
        }

        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponseDTO("Internal server error", 500))
                .build();
    }
}