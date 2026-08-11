package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bibliotech.Entidades.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

}