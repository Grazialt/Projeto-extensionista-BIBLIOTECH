package com.bibliotech.Controllers;

import com.bibliotech.DTOs.LoginRequest;
import com.bibliotech.DTOs.LoginResponse;
import com.bibliotech.DTOs.RegisterRequest;
import com.bibliotech.DTOs.UsuarioDTO;
import com.bibliotech.Services.UsuarioService;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.DTOs.CriarUsuarioRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String turma) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();

        if (q != null && !q.isBlank()) {
            usuarios = usuarios.stream()
                    .filter(u -> (u.getNome() != null && u.getNome().toLowerCase().contains(q.toLowerCase()))
                            || (u.getMatricula() != null && u.getMatricula().toLowerCase().contains(q.toLowerCase())))
                    .toList();
        }

        if (turma != null && !turma.isBlank()) {
            usuarios = usuarios.stream()
                    .filter(u -> turma.equalsIgnoreCase(u.getTurma()))
                    .toList();
        }

        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/usuarios")
public ResponseEntity<?> criarUsuario(
        @RequestBody CriarUsuarioRequest request,
        Authentication authentication) {

    boolean ehAdmin = authentication.getAuthorities().stream()
            .anyMatch(autoridade ->
                    autoridade.getAuthority().equals("ROLE_ADMIN"));

    if ("bibliotecario".equalsIgnoreCase(request.getTipo())
            && !ehAdmin) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Somente o ADMIN pode cadastrar bibliotecários.");
    }

    try {
        UsuarioDTO usuarioCriado =
                usuarioService.criarUsuario(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioCriado);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
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
public ResponseEntity<?> obterUsuario(
        @PathVariable Long id,
        Authentication authentication) {

    Usuario usuarioAutenticado =
            (Usuario) authentication.getPrincipal();

    boolean ehAdminOuBibliotecario =
            authentication.getAuthorities().stream()
                    .anyMatch(autoridade ->
                            autoridade.getAuthority().equals("ROLE_ADMIN")
                            || autoridade.getAuthority().equals("ROLE_BIBLIOTECARIO"));

    if (!ehAdminOuBibliotecario
            && !usuarioAutenticado.getId().equals(id)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    var usuarioOpt = usuarioService.obterUsuarioPorId(id);

    if (usuarioOpt.isPresent()) {
        return ResponseEntity.ok(usuarioOpt.get());
    }

    return ResponseEntity.notFound().build();
}

    @GetMapping("/usuarios/{id}/livros-lidos")
public ResponseEntity<?> obterQuantidadeLivrosLidos(
        @PathVariable Long id,
        Authentication authentication) {

    Usuario usuarioAutenticado =
            (Usuario) authentication.getPrincipal();

    boolean ehAdminOuBibliotecario =
            authentication.getAuthorities().stream()
                    .anyMatch(autoridade ->
                            autoridade.getAuthority().equals("ROLE_ADMIN")
                            || autoridade.getAuthority().equals("ROLE_BIBLIOTECARIO"));

    if (!ehAdminOuBibliotecario
            && !usuarioAutenticado.getId().equals(id)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    var totalOpt = usuarioService.obterQuantidadeLivrosLidos(id);

    if (totalOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(
            new LivrosLidosResponse(id, totalOpt.get())
    );
}

    private record LivrosLidosResponse(Long usuarioId, long quantidadeLivrosLidos) {}

   @GetMapping("/usuario/email/{email}")
public ResponseEntity<?> obterUsuarioPorEmail(
        @PathVariable String email,
        Authentication authentication) {

    boolean ehAdminOuBibliotecario =
            authentication.getAuthorities().stream()
                    .anyMatch(autoridade ->
                            autoridade.getAuthority().equals("ROLE_ADMIN")
                            || autoridade.getAuthority().equals("ROLE_BIBLIOTECARIO"));

    if (!ehAdminOuBibliotecario) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    var usuarioOpt = usuarioService.obterUsuarioPorEmail(email);

    if (usuarioOpt.isPresent()) {
        return ResponseEntity.ok(usuarioOpt.get());
    }

    return ResponseEntity.ok(
            new LoginResponse(false, "Usuário não encontrado")
    );
    }
    }