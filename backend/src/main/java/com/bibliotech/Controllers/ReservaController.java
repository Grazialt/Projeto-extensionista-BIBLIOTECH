package com.bibliotech.Controllers;

import com.bibliotech.DTOs.ReservaDTO;
import com.bibliotech.Services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    /**
     * Criar nova reserva
     * POST /api/reservas
     */
    @PostMapping
    public ResponseEntity<ReservaDTO> criarReserva(
            @RequestParam Long livroId,
            @RequestParam Long usuarioId) {
        ReservaDTO dto = reservaService.criarReserva(livroId, usuarioId);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Listar reservas do usuário
     * GET /api/reservas/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> listarReservasUsuario(
            @PathVariable Long usuarioId) {
        List<ReservaDTO> reservas = reservaService.listarReservasPorUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Cancelar reserva
     * DELETE /api/reservas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        boolean sucesso = reservaService.cancelarReserva(id);
        if (sucesso) {
            return ResponseEntity.ok(new Object() {
                public String mensagem = "Reserva cancelada com sucesso";
            });
        }
        return ResponseEntity.badRequest().body("Erro ao cancelar reserva");
    }

    /**
     * Obter reserva pelo ID
     * GET /api/reservas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obterReserva(@PathVariable Long id) {
        var dto = reservaService.obterReservaPorId(id);
        if (dto.isPresent()) {
            return ResponseEntity.ok(dto.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Listar todas as reservas
     * GET /api/reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarTodas() {
        List<ReservaDTO> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }
}
