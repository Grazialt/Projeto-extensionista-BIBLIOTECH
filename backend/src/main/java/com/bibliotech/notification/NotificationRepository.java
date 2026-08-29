package com.bibliotech.notification;

import com.bibliotech.Entidades.Usuario;;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

    List<Notification> findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(Usuario usuario);
}