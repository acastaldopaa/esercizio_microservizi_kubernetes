package it.paa.modelDTO;

import lombok.Data;

@Data
public class ProdottoDTO {

    private Long id;
    private String nome;
    private Double prezzo;

    public ProdottoDTO(){

    }

    public ProdottoDTO(Double prezzo, String nome) {
        this.prezzo = prezzo;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }
}
