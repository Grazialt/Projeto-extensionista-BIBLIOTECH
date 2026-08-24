package com.bibliotech.DTOs;

public class IndicadorDTO {
    private long totalLivros;
    private long exemplaresDisponiveis;
    private long usuariosCadastrados;
    private long emprestimosTotais;
    private long emprestimosAtivos;
    private long emprestimosAtrasados;
    private long livrosLidos;
    private double percentualDevolucoes;
    private double mediaLivrosLidosPorAluno;

    public IndicadorDTO() {}

    public IndicadorDTO(long totalLivros, long exemplaresDisponiveis, long usuariosCadastrados,
                        long emprestimosTotais, long emprestimosAtivos, long emprestimosAtrasados,
                        long livrosLidos, double percentualDevolucoes, double mediaLivrosLidosPorAluno) {
        this.totalLivros = totalLivros;
        this.exemplaresDisponiveis = exemplaresDisponiveis;
        this.usuariosCadastrados = usuariosCadastrados;
        this.emprestimosTotais = emprestimosTotais;
        this.emprestimosAtivos = emprestimosAtivos;
        this.emprestimosAtrasados = emprestimosAtrasados;
        this.livrosLidos = livrosLidos;
        this.percentualDevolucoes = percentualDevolucoes;
        this.mediaLivrosLidosPorAluno = mediaLivrosLidosPorAluno;
    }

    public long getTotalLivros() { return totalLivros; }
    public long getExemplaresDisponiveis() { return exemplaresDisponiveis; }
    public long getUsuariosCadastrados() { return usuariosCadastrados; }
    public long getEmprestimosTotais() { return emprestimosTotais; }
    public long getEmprestimosAtivos() { return emprestimosAtivos; }
    public long getEmprestimosAtrasados() { return emprestimosAtrasados; }
    public long getLivrosLidos() { return livrosLidos; }
    public double getPercentualDevolucoes() { return percentualDevolucoes; }
    public double getMediaLivrosLidosPorAluno() { return mediaLivrosLidosPorAluno; }
}
