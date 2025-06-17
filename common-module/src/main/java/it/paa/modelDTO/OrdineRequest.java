package it.paa.modelDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrdineRequest {
    @NotNull(message = "L'ID del prodotto è obbligatorio")
    @Positive(message = "L'ID del prodotto deve essere maggiore di zero")
    private Long prodottoId;
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    @Max(value = 1000, message = "La quantità massima consentita è 1000")
    private Integer quantita;

    public OrdineRequest() {
    }

    public OrdineRequest(Long prodottoId, Integer quantita) {
        this.prodottoId = prodottoId;
        this.quantita = quantita;
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
}
