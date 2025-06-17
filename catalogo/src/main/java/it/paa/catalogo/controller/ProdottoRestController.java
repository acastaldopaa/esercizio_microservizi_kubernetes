package it.paa.catalogo.controller;

import io.quarkus.resteasy.reactive.server.runtime.CloserImpl;
import io.quarkus.security.Authenticated;
import it.paa.catalogo.exception.DataProcessingException;
import it.paa.catalogo.model.Prodotto;
import it.paa.catalogo.service.ProdottoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import it.paa.catalogo.exception.GlobalExceptionHandler.ErrorResponse;

@Path( "/prodotti")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
@Authenticated
public class ProdottoRestController {

    @Inject
    ProdottoService service;
    @Inject
    CloserImpl closerImpl;

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {
            // Validazione dell'input
            if (id == null || id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Parametro non valido", "ID prodotto deve essere positivo"))
                        .build();
            }

            Prodotto prodotto = service.getById(id);
            if (prodotto == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Prodotto non trovato", "Nessun prodotto con ID: " + id))
                        .build();
            }

            return Response.ok(prodotto).build();
        } catch (Exception e) {
            // Il GlobalExceptionHandler gestirà questa eccezione
            throw new DataProcessingException("Errore nel recupero del prodotto ID: " + id, e);
        }

    }
}
