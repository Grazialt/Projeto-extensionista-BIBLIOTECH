package com.bibliotech.Entidades;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao")
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_real_devolucao")
    private LocalDate dataRealDevolucao;

    @Transient
    public LocalDate getDataDevolucao() {
        return dataPrevistaDevolucao;
    }

    @Transient
    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataPrevistaDevolucao = dataDevolucao;
    }

    private String status;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Livro livro;
}
