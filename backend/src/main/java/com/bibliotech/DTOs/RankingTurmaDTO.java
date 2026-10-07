package com.bibliotech.DTOs;

public class RankingTurmaDTO {
    private String turma;
    private long livrosLidos;
    private long alunosAtivos;

    public RankingTurmaDTO() {}

    public RankingTurmaDTO(String turma, long livrosLidos, long alunosAtivos) {
        this.turma = turma;
        this.livrosLidos = livrosLidos;
        this.alunosAtivos = alunosAtivos;
    }

    public String getTurma() { return turma; }
    public long getLivrosLidos() { return livrosLidos; }
    public long getAlunosAtivos() { return alunosAtivos; }
}
