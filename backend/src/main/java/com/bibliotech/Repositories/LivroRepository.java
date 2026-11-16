package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import com.bibliotech.Entidades.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Livro l where l.id = :id")
    Optional<Livro> buscarPorIdComBloqueio(@Param("id") Long id);


}