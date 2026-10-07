package com.bibliotech.Services;

import com.bibliotech.DTOs.LoginRequest;
import com.bibliotech.DTOs.LoginResponse;
import com.bibliotech.DTOs.RegisterRequest;
import com.bibliotech.DTOs.UsuarioDTO;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.UsuarioRepository;
import com.bibliotech.Repositories.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    /**
     * Realiza login do usuário
     */
    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            return new LoginResponse(false, "Email é obrigatório");
        }
        if (loginRequest.getSenha() == null || loginRequest.getSenha().isEmpty()) {
            return new LoginResponse(false, "Senha é obrigatória");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailIgnoreCase(loginRequest.getEmail().trim());

        if (usuarioOptional.isEmpty()) {
            return new LoginResponse(false, "Usuário não encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        if (!usuario.getSenha().equals(loginRequest.getSenha())) {
            return new LoginResponse(false, "Senha incorreta");
        }

        return new LoginResponse(true, "Login realizado com sucesso", converterParaDTO(usuario));
    }

    /**
     * Registra novo usuário
     */
    public LoginResponse register(RegisterRequest registerRequest) {
        if (registerRequest.getNome() == null || registerRequest.getNome().isEmpty()) {
            return new LoginResponse(false, "Nome é obrigatório");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            return new LoginResponse(false, "Email é obrigatório");
        }
        if (registerRequest.getSenha() == null || registerRequest.getSenha().isEmpty()) {
            return new LoginResponse(false, "Senha é obrigatória");
        }

        if (!registerRequest.getEmail().contains("@")) {
            return new LoginResponse(false, "Email inválido");
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmailIgnoreCase(registerRequest.getEmail().trim());
        if (usuarioExistente.isPresent()) {
            return new LoginResponse(false, "Email já cadastrado");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registerRequest.getNome());
        novoUsuario.setEmail(registerRequest.getEmail());
        novoUsuario.setSenha(registerRequest.getSenha());
        // O tipo "bibliotecario" é apenas para autenticação do painel administrativo.
        // Ele não deve entrar na base de alunos cadastrados nem interferir no acervo.
        novoUsuario.setTipo("bibliotecario");

        usuarioRepository.save(novoUsuario);

        return new LoginResponse(true, "Cadastro realizado com sucesso", converterParaDTO(novoUsuario));
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> !ehTipoSistema(usuario.getTipo()))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obterUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).map(this::converterParaDTO);
    }

    public Optional<UsuarioDTO> obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email.trim()).map(this::converterParaDTO);
    }

    public Optional<Long> obterQuantidadeLivrosLidos(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return Optional.empty();
        }
        return Optional.of(emprestimoRepository.countByUsuarioIdAndStatusIgnoreCase(usuarioId, "devolvido"));
    }

    public UsuarioDTO criarUsuario(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setMatricula(dto.getMatricula());
        usuario.setEndereco(dto.getEndereco());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setTipo(ehTipoSistema(dto.getTipo()) ? "aluno" : (dto.getTipo() != null ? dto.getTipo() : "aluno"));
        usuario.setTurma(dto.getTurma());
        usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }

    public Optional<UsuarioDTO> atualizarUsuario(Long id, UsuarioDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setNome(dto.getNome());
        usuario.setMatricula(dto.getMatricula());
        usuario.setEndereco(dto.getEndereco());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setTipo(dto.getTipo());
        usuario.setTurma(dto.getTurma());
        Usuario atualizado = usuarioRepository.save(usuario);
        return Optional.of(converterParaDTO(atualizado));
    }

    public boolean deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    private boolean ehTipoSistema(String tipo) {
        if (tipo == null) return false;
        String valor = tipo.trim().toLowerCase();
        return "admin".equals(valor) || "bibliotecario".equals(valor);
    }

    private UsuarioDTO converterParaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getMatricula(),
                usuario.getEndereco(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipo(),
                usuario.getTurma()
        );
        dto.setQuantidadeLivrosLidos(
                emprestimoRepository.countByUsuarioIdAndStatusIgnoreCase(usuario.getId(), "devolvido")
        );
        return dto;
    }
}
