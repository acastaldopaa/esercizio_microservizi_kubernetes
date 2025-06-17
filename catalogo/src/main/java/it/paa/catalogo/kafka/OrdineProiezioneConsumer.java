package it.paa.catalogo.kafka;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import it.paa.catalogo.exception.DataProcessingException;
import it.paa.catalogo.exception.InvalidOrderDataException;
import it.paa.catalogo.model.OrdineProdottoView;
import it.paa.catalogo.repository.OrdineProdottoViewRepository;
import it.paa.modelDTO.OrdineDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.Optional;

import static io.quarkus.arc.ComponentsProvider.LOG;

@ApplicationScoped
public class OrdineProiezioneConsumer {

    @Inject
    OrdineProdottoViewRepository viewRepository;

    @Incoming("ordini-finalizzati-in")
    @Blocking
    @Transactional
    public void aggiornaView(OrdineDTO ordineDTO) {
        String ordineId = Optional.ofNullable(ordineDTO)
                .map(dto -> String.valueOf(dto.getId()))
                .orElse("unknown");

        LOG.infof("Ricevuto nuovo ordine da processare: %s", ordineId);

        try {
            // Validazione dell'input
            validaOrdineDTO(ordineDTO);

            // Creazione della proiezione
            OrdineProdottoView proiezione = creaProiezione(ordineDTO);

            // Salvataggio nel database
            salvaProiezione(proiezione);

            LOG.infof("Proiezione aggiornata con successo per l'ordine ID: %s", ordineId);

        } catch (InvalidOrderDataException e) {
            LOG.errorf("Dati ordine non validi: %s - Ordine: %s", e.getMessage(), ordineDTO);
        } catch (DataProcessingException e) {
            LOG.errorf(e, "Errore nel processamento dell'ordine ID: %s", ordineId);
            throw e;
        } catch (Exception e) {
            LOG.errorf(e, "Errore imprevisto durante il processamento dell'ordine: %s", ordineId);
            throw new DataProcessingException("Errore imprevisto nel consumer Kafka", e);
        }
    }


    private void validaOrdineDTO(OrdineDTO ordineDTO) throws InvalidOrderDataException {
        // Per oggetti complessi, il controllo tradizionale è più chiaro
        if (ordineDTO == null) {
            throw new InvalidOrderDataException("OrdineDTO è null");
        }

        // Per i campi ID, possiamo usare Optional per una migliore gestione
        Long ordineId = Optional.ofNullable(ordineDTO.getId())
                .orElseThrow(() -> new InvalidOrderDataException("ID ordine è null"));

        // Controllo del prodotto
        Optional.ofNullable(ordineDTO.getProdottoDTO())
                .orElseThrow(() -> new InvalidOrderDataException("ProdottoDTO è null per l'ordine ID: " + ordineId));

        // Controllo ID prodotto
        Optional.ofNullable(ordineDTO.getProdottoDTO().getId())
                .orElseThrow(() -> new InvalidOrderDataException("ID prodotto è null per l'ordine ID: " + ordineId));

        // Per valori numerici, Optional è perfetto
        Integer quantita = Optional.ofNullable(ordineDTO.getQuantita()).orElse(0);
        if (quantita <= 0) {
            throw new InvalidOrderDataException("Quantità non valida per l'ordine ID: " + ordineId);
        }

        Double prezzoTotale = Optional.ofNullable(ordineDTO.getPrezzoTotale()).orElse(0.0);
        if (prezzoTotale <= 0) {
            throw new InvalidOrderDataException("Prezzo totale non valido per l'ordine ID: " + ordineId);
        }
    }


    private OrdineProdottoView creaProiezione(OrdineDTO ordineDTO) throws DataProcessingException {
        try {
            return new OrdineProdottoView(
                    ordineDTO.getProdottoDTO().getId(),
                    ordineDTO.getId(),
                    ordineDTO.getQuantita(),
                    ordineDTO.getPrezzoTotale()
            );
        } catch (Exception e) {
            throw new DataProcessingException("Errore nella creazione della proiezione per l'ordine ID: " + ordineDTO.getId(), e);
        }
    }

    private void salvaProiezione(OrdineProdottoView proiezione) throws DataProcessingException {
        try {
            viewRepository.saveOrUpdate(proiezione);
            Log.debugf("Proiezione salvata: ProdottoID=%d, OrdineID=%d",
                    proiezione.getProdottoId(), proiezione.getOrdineId());
        } catch (Exception e) {
            throw new DataProcessingException(
                    "Errore nel salvataggio della proiezione per ProdottoID: " + proiezione.getProdottoId() +
                            ", OrdineID: " + proiezione.getOrdineId(), e);
        }
    }


}
