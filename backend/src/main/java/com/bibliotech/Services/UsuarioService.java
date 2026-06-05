package com.bibliotech.Services;

import com.bibliotech.DTOs.LoginRequest;
import com.bibliotech.DTOs.LoginResponse;
import com.bibliotech.DTOs.RegisterRequest;
import com.bibliotech.DTOs.UsuarioDTO;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginRequest.getEmail());

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

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(registerRequest.getEmail());
        if (usuarioExistente.isPresent()) {
            return new LoginResponse(false, "Email já cadastrado");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registerRequest.getNome());
        novoUsuario.setMatricula(registerRequest.getMatricula());
        novoUsuario.setEndereco(registerRequest.getEndereco());
        novoUsuario.setEmail(registerRequest.getEmail());
        novoUsuario.setSenha(registerRequest.getSenha());
        novoUsuario.setTelefone(registerRequest.getTelefone());
        novoUsuario.setTipo("usuario");

        usuarioRepository.save(novoUsuario);

        return new LoginResponse(true, "Cadastro realizado com sucesso", converterParaDTO(novoUsuario));
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obterUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).map(this::converterParaDTO);
    }

    public Optional<UsuarioDTO> obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(this::converterParaDTO);
    }

    public UsuarioDTO criarUsuario(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setMatricula(dto.getMatricula());
        usuario.setEndereco(dto.getEndereco());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setTipo(dto.getTipo() != null ? dto.getTipo() : "usuario");
        if (dto.getTipo() == null) {
            usuario.setTipo("usuario");
        }
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

    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getMatricula(),
                usuario.getEndereco(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipo()
        );
    }
}
