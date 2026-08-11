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

    private LocalDate dataDevolucao;

    private String status;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Livro livro;
}
