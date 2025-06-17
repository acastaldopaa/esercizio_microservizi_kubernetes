package it.paa.catalogo.service;

import it.paa.catalogo.exception.DataProcessingException;
import it.paa.catalogo.model.Prodotto;
import it.paa.catalogo.repository.ProdottoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProdottoService {

    private static final Logger LOG = Logger.getLogger(ProdottoService.class);

    @Inject
    ProdottoRepository repository;

    public Prodotto getById(Long id) throws DataProcessingException {
        try {
            LOG.debugf("Recupero prodotto con ID: %d", id);
            Prodotto prodotto = repository.findById(id);

            if (prodotto != null) {
                LOG.debugf("Prodotto trovato: %s", prodotto.getNome());
            } else {
                LOG.debugf("Nessun prodotto trovato con ID: %d", id);
            }

            return prodotto;
        } catch (Exception e) {
            LOG.errorf(e, "Errore durante il recupero del prodotto ID: %d", id);
            throw new DataProcessingException("Errore nel database durante il recupero del prodotto", e);
        }
    }
}
