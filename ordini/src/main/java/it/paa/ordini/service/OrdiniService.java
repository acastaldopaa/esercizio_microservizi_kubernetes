package it.paa.ordini.service;

import it.paa.modelDTO.OrdineDTO;
import it.paa.modelDTO.ProdottoDTO;
import it.paa.ordini.controller.ProdottoRestClient;
import it.paa.ordini.exception.DataProcessingException;
import it.paa.ordini.exception.InvalidOrderDataException;
import it.paa.ordini.exception.ProductServiceException;
import it.paa.ordini.kafka.OrdineProducer;
import it.paa.ordini.model.Ordine;
import it.paa.ordini.repository.OrdineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

@ApplicationScoped
public class OrdiniService {

    private static final Logger LOG = Logger.getLogger(OrdiniService.class);

    @Inject
    OrdineRepository ordineRepository;

    @Inject
    OrdineProducer ordineProducer;

    @Inject
    @RestClient
    ProdottoRestClient prodottoRestClient;

    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 2000, successThreshold = 2)
    @Fallback(fallbackMethod = "fallbackCreaOrdine")
    public OrdineDTO creaOrdineCompleto(Long prodottoId, Integer quantita) throws InvalidOrderDataException, ProductServiceException {
        // 1. Transazione per la persistenza
        OrdineDTO ordineDTO = creaOrdineInTransazione(prodottoId, quantita);

        // 2. Invio asincrono FUORI dalla transazione
        ordineProducer.inviaOrdine(ordineDTO);

        return ordineDTO;
    }

    @Transactional
    public OrdineDTO creaOrdineInTransazione(Long prodottoId, Integer quantita) throws InvalidOrderDataException, ProductServiceException {
        try {
            LOG.debugf("Inizio creazione ordine per prodotto ID: %d, quantità: %d", prodottoId, quantita);

            // 1. Recupero prodotto
            ProdottoDTO prodottoDTO = recuperaProdotto(prodottoId);

            // 2. Validazione business
            validaCalcoloOrdine(prodottoDTO);

            // 3. Calcolo e creazione ordine
            Double prezzoTotale = prodottoDTO.getPrezzo() * quantita;

            Ordine ordine = new Ordine();
            ordine.setProdottoId(prodottoDTO.getId());
            ordine.setQuantita(quantita);
            ordine.setPrezzoTotale(prezzoTotale);

            // 4. Salvataggio
            ordineRepository.persist(ordine);
            LOG.debugf("Ordine salvato nel database: ID=%d", ordine.getId());

            // 5. Conversione a DTO
            OrdineDTO ordineDTO = new OrdineDTO();
            ordineDTO.setId(ordine.getId());
            ordineDTO.setProdottoDTO(prodottoDTO);
            ordineDTO.setQuantita(ordine.getQuantita());
            ordineDTO.setPrezzoTotale(ordine.getPrezzoTotale());

            LOG.infof("Ordine completato in transazione: ID=%d, Totale=%.2f", ordineDTO.getId(), ordineDTO.getPrezzoTotale());
            return ordineDTO;

        } catch (ProductServiceException | InvalidOrderDataException e) {
            throw e; // Propagazione diretta - no wrapping
        } catch (Exception e) {
            throw new DataProcessingException("Errore nella creazione dell'ordine", e);
        }

    }

    private ProdottoDTO recuperaProdotto(Long prodottoId) throws ProductServiceException {
        try {
            LOG.debugf("Recupero informazioni prodotto ID: %d", prodottoId);
            ProdottoDTO prodottoDTO = prodottoRestClient.getById(prodottoId);

            if (prodottoDTO == null) {
                throw new ProductServiceException("Prodotto non trovato con ID: " + prodottoId);
            }

            LOG.debugf("Prodotto recuperato: %s - Prezzo: %.2f",
                    prodottoDTO.getNome(), prodottoDTO.getPrezzo());
            return prodottoDTO;

        } catch (ClientWebApplicationException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 404) {
                LOG.warnf("Prodotto non trovato con ID: %d", prodottoId);
                throw new ProductServiceException("Prodotto non trovato con ID: " + prodottoId);
            }
            LOG.errorf(e, "Errore nella comunicazione con il servizio catalogo per prodotto ID: %d", prodottoId);
            throw new ProductServiceException("Errore nella comunicazione con il catalogo prodotti", e);
        } catch (ProductServiceException e) {
            throw e;
        } catch (Exception e) {
            LOG.errorf(e, "Errore generico nella comunicazione con il servizio catalogo per prodotto ID: %d", prodottoId);
            throw new ProductServiceException("Errore nella comunicazione con il catalogo prodotti", e);
        }
    }

    private void validaCalcoloOrdine(ProdottoDTO prodottoDTO) throws InvalidOrderDataException {
        if (prodottoDTO.getPrezzo() == null || prodottoDTO.getPrezzo() <= 0) {
            throw new InvalidOrderDataException("Prezzo prodotto non valido: " + prodottoDTO.getPrezzo());
        }
    }

    public OrdineDTO fallbackCreaOrdine(Long prodottoId, Integer quantita) throws ProductServiceException {
        LOG.warnf("Circuit Breaker ATTIVATO per prodotto ID: %d. Eseguo il metodo di fallback.", prodottoId);
        // Invece di far fallire la chiamata, restituiamo un'eccezione specifica e controllata.
        throw new ProductServiceException("Servizio prodotti momentaneamente non disponibile. Riprovare tra poco.");
    }
}