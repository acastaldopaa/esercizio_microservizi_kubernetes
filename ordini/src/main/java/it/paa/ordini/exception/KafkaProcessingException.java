package it.paa.ordini.exception;

public class KafkaProcessingException extends RuntimeException {
    public KafkaProcessingException(String message) {
        super(message);
    }

    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

