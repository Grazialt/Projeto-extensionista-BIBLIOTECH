package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotech.Entidades.Autor;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);
}
