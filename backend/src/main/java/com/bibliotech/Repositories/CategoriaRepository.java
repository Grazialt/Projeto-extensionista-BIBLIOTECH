package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotech.Entidades.Categoria;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
}
