package it.paa.ordini.kafka;

import io.smallrye.reactive.messaging.annotations.Blocking;
import it.paa.ordini.exception.DataProcessingException;
import it.paa.ordini.exception.KafkaProcessingException;
import it.paa.modelDTO.OrdineDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrdineFinalizzatoConsumer {

    private static final Logger LOG = Logger.getLogger(OrdineFinalizzatoConsumer.class);

    @Incoming("ordini-finalizzati-in")
    @Blocking
    public void riceviOrdineFinalizzato(OrdineDTO ordineDTO) {
        try {
            LOG.infof("Ordine finalizzato ricevuto: %s", ordineDTO);
            System.out.println("Ordine finalizzato: " + ordineDTO);
        } catch (Exception e) {
            LOG.errorf(e, "Errore nel processamento del messaggio Kafka per ordine: %s",
                    ordineDTO != null ? ordineDTO.getId() : "sconosciuto");
        }
    }
}