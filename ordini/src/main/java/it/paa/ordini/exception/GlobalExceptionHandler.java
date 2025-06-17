package it.paa.ordini.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.time.Instant;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.errorf(exception, "Eccezione non gestita negli ORDINI: %s", exception.getMessage());

        // UNWRAPPING: Se è DataProcessingException, controlla la causa
        if (exception instanceof DataProcessingException) {
            Throwable cause = exception.getCause();

            // Unwrap ProductServiceException
            if (cause instanceof ProductServiceException) {
                return handleProductServiceException((ProductServiceException) cause);
            }

            // Unwrap InvalidOrderDataException
            if (cause instanceof InvalidOrderDataException) {
                return handleInvalidOrderDataException((InvalidOrderDataException) cause);
            }

            // Unwrap ClientWebApplicationException
            if (cause instanceof ClientWebApplicationException) {
                return handleClientWebApplicationException((ClientWebApplicationException) cause);
            }

            // Se non c'è causa specifica, tratta come errore generico di processamento
            return handleDataProcessingException((DataProcessingException) exception);
        }

        if (exception instanceof ProductServiceException) {
            return handleProductServiceException((ProductServiceException) exception);
        }

        if (exception instanceof InvalidOrderDataException) {
            return handleInvalidOrderDataException((InvalidOrderDataException) exception);
        }

        if (exception instanceof ClientWebApplicationException) {
            return handleClientWebApplicationException((ClientWebApplicationException) exception);
        }

        if (exception instanceof NotFoundException) {
            LOG.warnf("Endpoint non trovato: %s", exception.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Endpoint non trovato", "L'endpoint richiesto non esiste"))
                    .build();
        }

        if (exception instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) exception);
        }

        LOG.errorf(exception, "Errore generico non gestito: %s", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Errore interno del server", "Si è verificato un errore imprevisto"))
                .build();
    }

    private Response handleProductServiceException(ProductServiceException ex) {
        if (ex.getMessage().contains("non trovato")) {
            LOG.warnf("Prodotto non trovato dal catalogo: %s", ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Prodotto non trovato", ex.getMessage()))
                    .build();
        }

        LOG.errorf(ex, "Errore comunicazione con servizio catalogo: %s", ex.getMessage());
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ErrorResponse("Servizio catalogo non disponibile", ex.getMessage()))
                .build();
    }

    private Response handleInvalidOrderDataException(InvalidOrderDataException ex) {
        LOG.warnf("Dati ordine non validi: %s", ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Dati ordine non validi", ex.getMessage()))
                .build();
    }

    private Response handleDataProcessingException(DataProcessingException ex) {
        LOG.errorf(ex, "Errore nel processamento ordine: %s", ex.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Errore nel processamento",
                        "Si è verificato un errore durante l'elaborazione dell'ordine"))
                .build();
    }

    private Response handleClientWebApplicationException(ClientWebApplicationException ex) {
        int statusCode = ex.getResponse().getStatus();

        if (statusCode == 404) {
            LOG.warnf("Prodotto non trovato nel catalogo (404): %s", ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Prodotto non trovato", "Il prodotto richiesto non esiste nel catalogo"))
                    .build();
        }

        if (statusCode >= 500) {
            LOG.errorf(ex, "Errore server del catalogo (%d): %s", statusCode, ex.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Servizio catalogo non disponibile",
                            "Il servizio catalogo è temporaneamente non disponibile"))
                    .build();
        }

        LOG.warnf("Errore client nella comunicazione con catalogo (%d): %s", statusCode, ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Errore comunicazione catalogo", ex.getMessage()))
                .build();
    }

    private Response handleConstraintViolationException(ConstraintViolationException cve) {
        String violations = cve.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        LOG.warnf("Errori di validazione Bean Validation: %s", violations);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Dati di input non validi", violations))
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