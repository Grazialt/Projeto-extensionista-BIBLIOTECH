package com.bibliotech.Entidades;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataReserva;

    private String status;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Livro livro;
}
