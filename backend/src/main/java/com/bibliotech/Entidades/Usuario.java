package com.bibliotech.Entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String matricula;

    private String endereco;

    @Column(unique = true)
    private String email;

    private String senha;

    private String telefone;

    private String tipo;


}