package it.paa.catalogo.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.errorf(exception, "Eccezione non gestita: %s", exception.getMessage());

        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Risorsa non trovata", "L'endpoint richiesto non esiste"))
                    .build();
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String message = cve.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Dati di input non validi", message))
                    .build();
        }

        /*if (exception instanceof InvalidOrderDataException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Dati ordine non validi", exception.getMessage()))
                    .build();
        }*/
        if (exception instanceof ProductNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Prodotto non trovato", exception.getMessage()))
                    .build();
        }

        if (exception instanceof DataProcessingException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Errore nel processamento", exception.getMessage()))
                    .build();
        }

        // Errore generico
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Errore interno del server", "Si è verificato un errore imprevisto"))
                .build();
    }

    public static class ErrorResponse {
        public String error;
        public String message;
        public String timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = Instant.now().toString();
        }
    }

}
