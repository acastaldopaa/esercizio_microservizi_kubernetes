package it.paa.ordini.model;

import jakarta.persistence.*;

@Entity
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long prodottoId;
    private Integer quantita;
    private Double prezzoTotale;

    public Ordine() {
    }

    public Ordine(Long prodottoId, Integer quantita, Double prezzoTotale) {
        this.prodottoId = prodottoId;
        this.quantita = quantita;
        this.prezzoTotale = prezzoTotale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
