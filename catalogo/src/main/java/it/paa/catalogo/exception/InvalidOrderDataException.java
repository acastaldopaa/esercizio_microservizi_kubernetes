package it.paa.catalogo.exception;

public class InvalidOrderDataException extends RuntimeException {

    public InvalidOrderDataException(String message) {
        super(message);
    }

    public InvalidOrderDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
