package it.paa.catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class OrdineProdottoView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ordineId;
    private Long prodottoId;
    private Integer quantita;
    private Double prezzoTotale;

    public OrdineProdottoView() {
    }

    public OrdineProdottoView(Long prodottoId, Long ordineId, Integer quantita, Double prezzoTotale) {
        this.prodottoId = prodottoId;
        this.ordineId = ordineId;
        this.quantita = quantita;
        this.prezzoTotale = prezzoTotale;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrdineId() {
        return ordineId;
    }
    public void setOrdineId(Long ordineId) {
        this.ordineId = ordineId;
    }
    public Long getProdottoId() {
        return prodottoId;
    }
    public void setProdottoId(Long prodottoId) {
        this.prodottoId = prodottoId;
    }
    public Integer getQuantita() {
        return quantita;
    }
    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }
    public Double getPrezzoTotale() {
        return prezzoTotale;
    }
    public void setPrezzoTotale(Double prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }
}
