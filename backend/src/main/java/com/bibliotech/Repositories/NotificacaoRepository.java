package com.bibliotech.Repositories;

import com.bibliotech.Entidades.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);
    long countByUsuarioIdAndLidaFalse(Long usuarioId);
    Optional<Notificacao> findByIdAndUsuarioId(Long id, Long usuarioId);
    List<Notificacao> findByUsuarioIdAndLidaFalse(Long usuarioId);
    boolean existsByChaveUnica(String chaveUnica);
}
