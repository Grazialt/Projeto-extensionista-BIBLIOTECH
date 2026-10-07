package com.bibliotech.Services;

import com.bibliotech.DTOs.LoginRequest;
import com.bibliotech.DTOs.LoginResponse;
import com.bibliotech.DTOs.UsuarioDTO;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.EmprestimoRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService();
        org.springframework.test.util.ReflectionTestUtils.setField(service, "usuarioRepository", usuarioRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "emprestimoRepository", emprestimoRepository);
    }

    @Test
    void listarTodosDeveIgnorarTiposDeSistema() {
        Usuario aluno = new Usuario();
        aluno.setId(1L);
        aluno.setNome("Aluno");
        aluno.setTipo("aluno");

        Usuario bibliotecario = new Usuario();
        bibliotecario.setId(2L);
        bibliotecario.setNome("Bibliotecario");
        bibliotecario.setTipo("bibliotecario");

        Usuario admin = new Usuario();
        admin.setId(3L);
        admin.setNome("Admin");
        admin.setTipo("admin");

        when(usuarioRepository.findAll()).thenReturn(List.of(aluno, bibliotecario, admin));

        List<UsuarioDTO> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Aluno", resultado.get(0).getNome());
    }

    @Test
    void loginDeveEncontrarEmailSemDiferenciarMaiusculas() {
        Usuario usuario = new Usuario();
        usuario.setId(4L);
        usuario.setNome("Kaka");
        usuario.setEmail("Kaka@gmail.com");
        usuario.setSenha("senha-teste");
        usuario.setTipo("bibliotecario");
        when(usuarioRepository.findByEmailIgnoreCase("kaka@gmail.com")).thenReturn(Optional.of(usuario));
        when(emprestimoRepository.countByUsuarioIdAndStatusIgnoreCase(4L, "devolvido")).thenReturn(0L);

        LoginResponse resposta = service.login(new LoginRequest("kaka@gmail.com", "senha-teste"));

        assertTrue(resposta.isSucesso());
        assertEquals("Kaka@gmail.com", resposta.getUsuario().getEmail());
        verify(usuarioRepository).findByEmailIgnoreCase("kaka@gmail.com");
    }
}
