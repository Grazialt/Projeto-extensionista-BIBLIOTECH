package com.bibliotech.Controllers;

import com.bibliotech.DTOs.NotificacaoDTO;
import com.bibliotech.Services.NotificacaoService;
import com.bibliotech.Services.AlertaEmprestimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final AlertaEmprestimoService alertaEmprestimoService;

    public NotificacaoController(
            NotificacaoService notificacaoService,
            AlertaEmprestimoService alertaEmprestimoService) {
        this.notificacaoService = notificacaoService;
        this.alertaEmprestimoService = alertaEmprestimoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoDTO>> listar(@PathVariable Long usuarioId) {
        alertaEmprestimoService.verificarUsuario(usuarioId);
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas/count")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(Map.of("quantidade", notificacaoService.contarNaoLidas(usuarioId)));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<NotificacaoDTO> marcarComoLida(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        return notificacaoService.marcarComoLida(id, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/usuario/{usuarioId}/lidas")
    public ResponseEntity<Map<String, Integer>> marcarTodasComoLidas(@PathVariable Long usuarioId) {
        int quantidade = notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok(Map.of("quantidadeAtualizada", quantidade));
    }
}
