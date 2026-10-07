package com.bibliotech.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDTO {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensagem;
    private String tipo;
    private boolean lida;
    private LocalDateTime dataCriacao;
}
