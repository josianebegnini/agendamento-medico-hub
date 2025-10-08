package com.example.mspaciente.exceptions;

/**
 * Lançada para erros de regras de negócio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}