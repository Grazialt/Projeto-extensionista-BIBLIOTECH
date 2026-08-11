package com.bibliotech.Controllers;

import com.bibliotech.DTOs.EmprestimoDTO;
import com.bibliotech.DTOs.EmprestimoRequest;
import com.bibliotech.Services.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = "*")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> criarEmprestimo(@RequestBody EmprestimoRequest request) {
        EmprestimoDTO dto = emprestimoService.criarEmprestimo(request.getLivroId(), request.getUsuarioId(), request.getDataDevolucao());
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EmprestimoDTO>> listarEmprestimosUsuario(@PathVariable Long usuarioId) {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarEmprestimosPorUsuario(usuarioId);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<EmprestimoDTO>> listarEmprestimosPorStatus(@RequestParam String status) {
        if (status.equalsIgnoreCase("atrasado")) {
            return ResponseEntity.ok(emprestimoService.listarAtrasados());
        }
        return ResponseEntity.ok(emprestimoService.listarPorStatus(status));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolverEmprestimo(@PathVariable Long id) {
        boolean sucesso = emprestimoService.devolverEmprestimo(id);
        if (sucesso) {
            return ResponseEntity.ok(new Object() {
                public String mensagem = "Livro devolvido com sucesso";
            });
        }
        return ResponseEntity.badRequest().body("Erro ao devolver livro");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoDTO> obterEmprestimo(@PathVariable Long id) {
        var dto = emprestimoService.obterEmprestimoPorId(id);
        if (dto.isPresent()) {
            return ResponseEntity.ok(dto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoDTO>> listarTodos(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            if (status.equalsIgnoreCase("atrasado")) {
                return ResponseEntity.ok(emprestimoService.listarAtrasados());
            }
            return ResponseEntity.ok(emprestimoService.listarPorStatus(status));
        }
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }
}
