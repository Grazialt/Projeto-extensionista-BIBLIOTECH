package com.bibliotech.DTOs;

public class RankingDTO {
    private String nome;
    private String titulo;
    private long quantidade;

    public RankingDTO() {}

    public RankingDTO(String nome, String titulo, long quantidade) {
        this.nome = nome;
        this.titulo = titulo;
        this.quantidade = quantidade;
    }

    public String getNome() { return nome; }
    public String getTitulo() { return titulo; }
    public long getQuantidade() { return quantidade; }
}
