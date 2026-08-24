package com.bibliotech.Controllers;

import com.bibliotech.DTOs.EvolucaoMensalDTO;
import com.bibliotech.DTOs.IndicadorDTO;
import com.bibliotech.DTOs.RankingDTO;
import com.bibliotech.Services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obterRelatorio() {
        return ResponseEntity.ok(Map.of(
                "indicadores", relatorioService.indicadores(),
                "rankingAlunos", relatorioService.rankingAlunos(),
                "livrosMaisEmprestados", relatorioService.livrosMaisEmprestados(),
                "evolucaoMensal", relatorioService.evolucaoMensal()
        ));
    }

    @GetMapping("/indicadores")
    public ResponseEntity<IndicadorDTO> indicadores() {
        return ResponseEntity.ok(relatorioService.indicadores());
    }

    @GetMapping("/ranking-alunos")
    public ResponseEntity<List<RankingDTO>> rankingAlunos() {
        return ResponseEntity.ok(relatorioService.rankingAlunos());
    }

    @GetMapping("/livros-mais-emprestados")
    public ResponseEntity<List<RankingDTO>> livrosMaisEmprestados() {
        return ResponseEntity.ok(relatorioService.livrosMaisEmprestados());
    }

    @GetMapping("/evolucao-mensal")
    public ResponseEntity<List<EvolucaoMensalDTO>> evolucaoMensal() {
        return ResponseEntity.ok(relatorioService.evolucaoMensal());
    }
}
