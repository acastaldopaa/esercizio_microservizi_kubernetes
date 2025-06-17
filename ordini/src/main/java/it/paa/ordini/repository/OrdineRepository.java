package it.paa.ordini.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import it.paa.ordini.model.Ordine;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrdineRepository implements PanacheRepository<Ordine> {

}