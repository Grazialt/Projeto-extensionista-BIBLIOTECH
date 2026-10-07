package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.bibliotech.Entidades.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    long countByTipoIgnoreCase(String tipo);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE COALESCE(LOWER(u.tipo), '') NOT IN ('admin', 'bibliotecario')")
    long countAlunos();
}