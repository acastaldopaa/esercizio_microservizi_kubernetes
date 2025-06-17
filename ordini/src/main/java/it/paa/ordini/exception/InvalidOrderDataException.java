package it.paa.ordini.exception;

public class InvalidOrderDataException extends Exception {
    public InvalidOrderDataException(String message) {
        super(message);
    }

    public InvalidOrderDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
