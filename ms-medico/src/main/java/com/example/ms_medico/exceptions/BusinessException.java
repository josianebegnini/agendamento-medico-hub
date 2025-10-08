package com.example.ms_medico.exceptions;

/**
 * Lançada para erros de regras de negócio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}