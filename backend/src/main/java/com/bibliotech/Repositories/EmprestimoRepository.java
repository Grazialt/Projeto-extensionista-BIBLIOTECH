package com.bibliotech.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import com.bibliotech.Entidades.Emprestimo;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Emprestimo e where e.id = :id")
    Optional<Emprestimo> buscarPorIdComBloqueio(@Param("id") Long id);


    long countByUsuarioIdAndStatusIgnoreCase(Long usuarioId, String status);

    long countByStatusIgnoreCase(String status);

    List<Emprestimo> findByStatusIgnoreCase(String status);

    List<Emprestimo> findByUsuarioIdAndStatusIgnoreCase(Long usuarioId, String status);


@Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
    FROM Emprestimo e
    WHERE e.livro.id = :livroId
""")
boolean existeEmprestimoDoLivro(@Param("livroId") Long livroId);

}
