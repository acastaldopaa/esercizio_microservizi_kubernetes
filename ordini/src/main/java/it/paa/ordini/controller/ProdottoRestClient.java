package it.paa.ordini.controller;

import it.paa.modelDTO.ProdottoDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/prodotti")
@RegisterRestClient(configKey = "prodotti-api")
public interface ProdottoRestClient {

    @GET
    @Path("/{id}")
    @Produces("application/json")
    ProdottoDTO getById(@PathParam("id") Long id);
}
