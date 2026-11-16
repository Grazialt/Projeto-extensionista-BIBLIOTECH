
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
private JwtService jwtService;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService();

        ReflectionTestUtils.setField(service, "usuarioRepository", usuarioRepository);
        ReflectionTestUtils.setField(service, "emprestimoRepository", emprestimoRepository);
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(service, "jwtService", jwtService);
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

        when(usuarioRepository.findAll())
                .thenReturn(List.of(aluno, bibliotecario, admin));

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
        usuario.setSenha("senha-criptografada");
        usuario.setTipo("bibliotecario");

        when(usuarioRepository.findByEmailIgnoreCase("kaka@gmail.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("senha-teste", "senha-criptografada"))
                .thenReturn(true);

                when(jwtService.gerarToken(usuario))
        .thenReturn("token-teste");

        when(emprestimoRepository.countByUsuarioIdAndStatusIgnoreCase(4L, "devolvido"))
                .thenReturn(0L);

        LoginResponse resposta = service.login(
                new LoginRequest("kaka@gmail.com", "senha-teste")
        );

        assertTrue(resposta.isSucesso());
        assertEquals("Kaka@gmail.com", resposta.getUsuario().getEmail());

        verify(usuarioRepository).findByEmailIgnoreCase("kaka@gmail.com");
        verify(passwordEncoder).matches("senha-teste", "senha-criptografada");
    }
}
