package it.paa.catalogo.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.paa.catalogo.model.Prodotto;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdottoRepository implements PanacheRepository<Prodotto> {

}
