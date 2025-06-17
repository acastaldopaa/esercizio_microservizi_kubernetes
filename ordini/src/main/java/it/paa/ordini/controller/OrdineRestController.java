package it.paa.ordini.controller;

import io.quarkus.security.Authenticated;
import it.paa.modelDTO.OrdineDTO;
import it.paa.modelDTO.OrdineRequest;
import it.paa.modelDTO.ProdottoDTO;
import it.paa.ordini.exception.DataProcessingException;
import it.paa.ordini.exception.InvalidOrderDataException;
import it.paa.ordini.exception.ProductServiceException;
import it.paa.ordini.exception.GlobalExceptionHandler.ErrorResponse;
import it.paa.ordini.response.SuccessResponse;
import it.paa.ordini.service.OrdiniService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/ordini")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
@Authenticated
public class OrdineRestController {

    private static final Logger LOG = Logger.getLogger(OrdineRestController.class);

    @Inject
    OrdiniService ordiniService;

    @POST
    public Response creaOrdine(@Valid OrdineRequest richiesta) {
        LOG.infof("Ricevuta richiesta creazione ordine per prodotto ID: %d, quantità: %d",
                richiesta.getProdottoId(), richiesta.getQuantita());

        try {
            // Tutta la logica delegata al service
            OrdineDTO ordineDTO = ordiniService.creaOrdineCompleto(richiesta.getProdottoId(), richiesta.getQuantita());

            LOG.infof("Ordine creato con successo: ID=%d, Totale=%.2f",
                    ordineDTO.getId(), ordineDTO.getPrezzoTotale());

            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse("Ordine creato", "Ordine ID: " + ordineDTO.getId()))
                    .build();
        } catch (InvalidOrderDataException e) {
            LOG.warnf("Dati ordine non validi: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Dati ordine non validi", e.getMessage()))
                    .build();

        } catch (ProductServiceException e) {
            LOG.errorf(e, "Errore nel servizio prodotti: %s", e.getMessage());
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(new ErrorResponse("Servizio prodotti non disponibile", e.getMessage()))
                    .build();

        } catch (Exception e) {
            LOG.errorf(e, "Errore imprevisto nella creazione ordine: %s", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Errore interno", "Si è verificato un errore imprevisto"))
                    .build();
        }

    }
}