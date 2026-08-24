package com.bibliotech.DTOs;

public class EvolucaoMensalDTO {
    private String mes;
    private long quantidade;

    public EvolucaoMensalDTO() {}

    public EvolucaoMensalDTO(String mes, long quantidade) {
        this.mes = mes;
        this.quantidade = quantidade;
    }

    public String getMes() { return mes; }
    public long getQuantidade() { return quantidade; }
}
