package com.bibliotech.DTOs;

import java.time.LocalDate;

public class EmprestimoRequest {
    private Long livroId;
    private Long usuarioId;
    private LocalDate dataDevolucao;

    public EmprestimoRequest() {}

    public EmprestimoRequest(Long livroId, Long usuarioId, LocalDate dataDevolucao) {
        this.livroId = livroId;
        this.usuarioId = usuarioId;
        this.dataDevolucao = dataDevolucao;
    }

    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
}
