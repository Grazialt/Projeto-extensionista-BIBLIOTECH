package com.bibliotech.Entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(name = "chave_unica", unique = true, length = 180)
    private String chaveUnica;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;
}
