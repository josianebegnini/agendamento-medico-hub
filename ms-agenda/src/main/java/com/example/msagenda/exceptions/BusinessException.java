package com.example.msagenda.exceptions;

/**
 * Lançada para erros de regras de negócio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}