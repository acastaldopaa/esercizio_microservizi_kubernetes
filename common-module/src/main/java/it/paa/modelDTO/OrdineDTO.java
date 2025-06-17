package it.paa.modelDTO;

import lombok.Data;

@Data
public class OrdineDTO {

    private Long id;
    private ProdottoDTO prodottoDTO;
    private Integer quantita;
    private Double prezzoTotale;

    public OrdineDTO() {
    }

    public OrdineDTO(ProdottoDTO prodottoDTO, Integer quantita, double prezzoTotale) {
        this.prodottoDTO = prodottoDTO;
        this.quantita = quantita;
        this.prezzoTotale = prezzoTotale;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProdottoDTO(ProdottoDTO prodottoDTO) {
        this.prodottoDTO = prodottoDTO;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public void setPrezzoTotale(Double prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }
}
