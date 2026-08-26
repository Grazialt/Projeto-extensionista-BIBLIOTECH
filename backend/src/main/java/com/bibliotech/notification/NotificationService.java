package com.bibliotech.notification;

import com.bibliotech.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification criar(
            Usuario usuario,
            String titulo,
            String mensagem) {

        Notification notification = new Notification();

        notification.setUsuario(usuario);
        notification.setTitulo(titulo);
        notification.setMensagem(mensagem);
        notification.setLida(false);
        notification.setDataCriacao(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> listarPorUsuario(Usuario usuario) {

        return notificationRepository
                .findByUsuarioOrderByDataCriacaoDesc(usuario);
    }

    public List<Notification> listarNaoLidas(Usuario usuario) {

        return notificationRepository
                .findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);
    }

    public Notification marcarComoLida(Long id) {

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notificação não encontrada"));

        notification.setLida(true);

        return notificationRepository.save(notification);
    }

    public void marcarTodasComoLidas(Usuario usuario) {

        List<Notification> notificacoes =
                notificationRepository.findByUsuarioAndLidaFalseOrderByDataCriacaoDesc(usuario);

        for (Notification notification : notificacoes) {
            notification.setLida(true);
        }

        notificationRepository.saveAll(notificacoes);
    }

    public void excluir(Long id) {

        notificationRepository.deleteById(id);
    }
}