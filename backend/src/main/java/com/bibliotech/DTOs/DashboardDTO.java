package com.bibliotech.DTOs;

public class DashboardDTO {
    private long totalLivros;
    private long usuariosCadastrados;
    private long emprestimosAtivos;
    private long emprestimosAtrasados;
    private long livrosDevolvidos;

    public DashboardDTO() {}

    public DashboardDTO(long totalLivros, long usuariosCadastrados, long emprestimosAtivos,
                        long emprestimosAtrasados, long livrosDevolvidos) {
        this.totalLivros = totalLivros;
        this.usuariosCadastrados = usuariosCadastrados;
        this.emprestimosAtivos = emprestimosAtivos;
        this.emprestimosAtrasados = emprestimosAtrasados;
        this.livrosDevolvidos = livrosDevolvidos;
    }

    public long getTotalLivros() {
        return totalLivros;
    }

    public void setTotalLivros(long totalLivros) {
        this.totalLivros = totalLivros;
    }

    public long getUsuariosCadastrados() {
        return usuariosCadastrados;
    }

    public void setUsuariosCadastrados(long usuariosCadastrados) {
        this.usuariosCadastrados = usuariosCadastrados;
    }

    public long getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void setEmprestimosAtivos(long emprestimosAtivos) {
        this.emprestimosAtivos = emprestimosAtivos;
    }

    public long getEmprestimosAtrasados() {
        return emprestimosAtrasados;
    }

    public void setEmprestimosAtrasados(long emprestimosAtrasados) {
        this.emprestimosAtrasados = emprestimosAtrasados;
    }

    public long getLivrosDevolvidos() {
        return livrosDevolvidos;
    }

    public void setLivrosDevolvidos(long livrosDevolvidos) {
        this.livrosDevolvidos = livrosDevolvidos;
    }
}
