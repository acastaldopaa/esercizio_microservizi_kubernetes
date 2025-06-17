package it.paa.catalogo.controller;

import io.quarkus.security.Authenticated;
import it.paa.catalogo.exception.DataProcessingException;
import it.paa.catalogo.model.OrdineProdottoView;
import it.paa.catalogo.repository.OrdineProdottoViewRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import it.paa.catalogo.exception.GlobalExceptionHandler.ErrorResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path( "/ordini-prodotto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Authenticated
public class OrdineProdottoResource {

    private static final Logger LOG = Logger.getLogger(OrdineProdottoResource.class);

    @Inject
    OrdineProdottoViewRepository repository;

    @GET
    @Path("/{prodottoId}")
    public Response getOrdiniByProdotto(@PathParam("prodottoId") Long prodottoId){
        try{
            // Validazione dell'input
            if (prodottoId == null || prodottoId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Parametro non valido", "ID prodotto deve essere positivo"))
                        .build();
            }

            List<OrdineProdottoView> ordini = repository.findByProdottoId(prodottoId);

            LOG.debugf("Trovati %d ordini per il prodotto ID: %d", ordini.size(), prodottoId);

            return Response.ok(ordini).build();

        } catch (Exception e) {
            LOG.errorf(e, "Errore nel recupero ordini per prodotto ID: %d", prodottoId);
            throw new DataProcessingException("Errore nel recupero degli ordini per il prodotto ID: " + prodottoId, e);
        }
    }
}
