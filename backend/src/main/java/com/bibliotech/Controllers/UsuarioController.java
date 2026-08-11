package com.bibliotech.Controllers;

import com.bibliotech.DTOs.LoginRequest;
import com.bibliotech.DTOs.LoginResponse;
import com.bibliotech.DTOs.RegisterRequest;
import com.bibliotech.DTOs.UsuarioDTO;
import com.bibliotech.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = usuarioService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        LoginResponse response = usuarioService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(@RequestParam(required = false) String q) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        if (q != null && !q.isBlank()) {
            usuarios = usuarios.stream()
                    .filter(u -> (u.getNome() != null && u.getNome().toLowerCase().contains(q.toLowerCase()))
                            || (u.getMatricula() != null && u.getMatricula().toLowerCase().contains(q.toLowerCase())))
                    .toList();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO criado = usuarioService.criarUsuario(usuarioDTO);
        return ResponseEntity.ok(criado);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.atualizarUsuario(id, usuarioDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        if (usuarioService.deletarUsuario(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> obterUsuario(@PathVariable Long id) {
        var usuarioOpt = usuarioService.obterUsuarioPorId(id);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios/{id}/livros-lidos")
    public ResponseEntity<?> obterQuantidadeLivrosLidos(@PathVariable Long id) {
        var totalOpt = usuarioService.obterQuantidadeLivrosLidos(id);
        if (totalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new LivrosLidosResponse(id, totalOpt.get()));
    }

    private record LivrosLidosResponse(Long usuarioId, long quantidadeLivrosLidos) {}

    @GetMapping("/usuario/email/{email}")
    public ResponseEntity<?> obterUsuarioPorEmail(@PathVariable String email) {
        var usuarioOpt = usuarioService.obterUsuarioPorEmail(email);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        }
        return ResponseEntity.ok(new LoginResponse(false, "Usuário não encontrado"));
    }
}
