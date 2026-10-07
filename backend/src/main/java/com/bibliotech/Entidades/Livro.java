package com.bibliotech.Entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String isbn;

    private Integer anoPublicacao;

    private Integer quantidade;

    private String editora;

    private String edicao;

    private Integer paginas;

    @ManyToOne
    private Autor autor;

    @ManyToOne
    private Categoria categoria;
}
