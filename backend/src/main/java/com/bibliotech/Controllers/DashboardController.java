package com.bibliotech.Controllers;

import com.bibliotech.DTOs.DashboardDTO;
import com.bibliotech.Repositories.EmprestimoRepository;
import com.bibliotech.Repositories.LivroRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping
    public ResponseEntity<DashboardDTO> obterDashboard() {
        long totalLivros = livroRepository.count();
        long usuariosCadastrados = usuarioRepository.count();
        long emprestimosAtivos = emprestimoRepository.findAll().stream()
                .filter(e -> "ativo".equalsIgnoreCase(e.getStatus()))
                .count();
        long emprestimosAtrasados = emprestimoRepository.findAll().stream()
                .filter(e -> "ativo".equalsIgnoreCase(e.getStatus()) && e.getDataDevolucao() != null && e.getDataDevolucao().isBefore(LocalDate.now()))
                .count();
        long livrosDevolvidos = emprestimoRepository.countByStatusIgnoreCase("devolvido");
        long totalLivrosLidos = livrosDevolvidos;

        DashboardDTO dto = new DashboardDTO(totalLivros, usuariosCadastrados, emprestimosAtivos,
                emprestimosAtrasados, livrosDevolvidos, totalLivrosLidos);
        return ResponseEntity.ok(dto);
    }
}
