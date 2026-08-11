package com.bibliotech.Controllers;

import com.bibliotech.DTOs.LivroDTO;
import com.bibliotech.Services.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public ResponseEntity<List<LivroDTO>> listarLivros(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(livroService.listarTodos());
        }
        return ResponseEntity.ok(livroService.buscar(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> obterLivro(@PathVariable Long id) {
        return livroService.obterPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LivroDTO> criarLivro(@RequestBody LivroDTO livroDTO) {
        LivroDTO criado = livroService.criar(livroDTO);
        return ResponseEntity.ok(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroDTO> atualizarLivro(@PathVariable Long id, @RequestBody LivroDTO livroDTO) {
        return livroService.atualizar(id, livroDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarLivro(@PathVariable Long id) {
        if (livroService.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
