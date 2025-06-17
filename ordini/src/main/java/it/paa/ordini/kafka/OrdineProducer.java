package it.paa.ordini.kafka;

import io.smallrye.mutiny.Uni;
import it.paa.ordini.exception.KafkaProcessingException;
import it.paa.modelDTO.OrdineDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.time.Duration;

@ApplicationScoped
public class OrdineProducer {

    private static final Logger LOG = Logger.getLogger(OrdineProducer.class);

    @Inject
    @Channel("ordini-finalizzati-out")
    Emitter<OrdineDTO> emitter;

    public void inviaOrdine(OrdineDTO ordineDTO) {
        try {
            LOG.debugf("Invio ordine al topic Kafka: ID=%d", ordineDTO.getId());
            emitter.send(ordineDTO);
            LOG.infof("Ordine inviato con successo al topic Kafka: ID=%d", ordineDTO.getId());
        } catch (Exception e) {
            // ⚠️ Non lanciare eccezione - l'ordine è già salvato
            LOG.errorf(e, "Errore nell'invio dell'ordine ID: %d al topic Kafka", ordineDTO.getId());
        }
    }
    public Uni<Void> inviaOrdineReactive(OrdineDTO ordineDTO) {
        return Uni.createFrom().completionStage(() -> {
            try {
                LOG.debugf("Invio ordine reattivo: ID=%d", ordineDTO.getId());
                return emitter.send(ordineDTO);
            } catch (Exception e) {
                LOG.errorf(e, "Errore nell'invio reattivo dell'ordine ID: %d", ordineDTO.getId());
                throw new RuntimeException("Errore invio Kafka", e);
            }
        });
    }
}