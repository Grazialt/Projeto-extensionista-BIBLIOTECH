package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotech.Entidades.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    long countByUsuarioIdAndStatusIgnoreCase(Long usuarioId, String status);

    long countByStatusIgnoreCase(String status);
}
