package com.bibliotech.DTOs;

import java.time.LocalDate;

public class ReservaDTO {
    private Long id;
    private Long livroId;
    private Long usuarioId;
    private LocalDate dataReserva;
    private String status;
    private String livroTitulo;
    private String usuarioNome;

    public ReservaDTO() {}

    public ReservaDTO(Long id, Long livroId, Long usuarioId, LocalDate dataReserva, String status) {
        this.id = id;
        this.livroId = livroId;
        this.usuarioId = usuarioId;
        this.dataReserva = dataReserva;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLivroTitulo() { return livroTitulo; }
    public void setLivroTitulo(String livroTitulo) { this.livroTitulo = livroTitulo; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
}
