package com.bibliotech.notification;

import com.bibliotech.Entidades.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Notification>> listar(
            @PathVariable Long usuarioId) {

        // Temporariamente vamos buscar o usuário pelo ID.
        // Essa parte será adaptada ao seu UsuarioRepository.
        return ResponseEntity.ok(List.of());
    }
}