package it.paa.catalogo.exception;

public class ProductServiceException extends Exception {
    public ProductServiceException(String message) {
        super(message);
    }

    public ProductServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}