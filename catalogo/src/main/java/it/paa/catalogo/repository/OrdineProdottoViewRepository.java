package it.paa.catalogo.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.paa.catalogo.model.OrdineProdottoView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class OrdineProdottoViewRepository implements PanacheRepository<OrdineProdottoView> {

    @Transactional
    public void saveOrUpdate(OrdineProdottoView view){
        persist(view);
    }

    public List<OrdineProdottoView> findByProdottoId(Long prodottoId){
        return list("prodottoId", prodottoId);
    }
}
