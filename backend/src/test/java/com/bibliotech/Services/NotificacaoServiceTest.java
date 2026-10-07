package com.bibliotech.Services;

import com.bibliotech.DTOs.NotificacaoDTO;
import com.bibliotech.Entidades.Notificacao;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.NotificacaoRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {
    @Mock NotificacaoRepository notificacaoRepository;
    @Mock UsuarioRepository usuarioRepository;
    private NotificacaoService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new NotificacaoService(notificacaoRepository, usuarioRepository);
        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Alan");
    }

    @Test
    void criarDevePersistirNotificacaoComoNaoLida() {
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(inv -> {
            Notificacao n = inv.getArgument(0);
            n.setId(10L);
            return n;
        });

        NotificacaoDTO dto = service.criar(usuario, "Empréstimo realizado", "Livro emprestado", "EMPRESTIMO");

        assertEquals(10L, dto.getId());
        assertFalse(dto.isLida());
        assertEquals(7L, dto.getUsuarioId());
        assertEquals("EMPRESTIMO", dto.getTipo());
        assertNotNull(dto.getDataCriacao());
        verify(notificacaoRepository).save(any(Notificacao.class));
    }

    @Test
    void listarPorUsuarioDeveManterOrdemDoRepository() {
        Notificacao n = new Notificacao();
        n.setId(1L); n.setUsuario(usuario); n.setTitulo("Teste"); n.setMensagem("Mensagem"); n.setTipo("SISTEMA");
        when(notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(7L)).thenReturn(List.of(n));

        List<NotificacaoDTO> resultado = service.listarPorUsuario(7L);

        assertEquals(1, resultado.size());
        assertEquals("Teste", resultado.get(0).getTitulo());
    }

    @Test
    void marcarComoLidaSoDeveAlterarNotificacaoDoUsuario() {
        Notificacao n = new Notificacao();
        n.setId(3L); n.setUsuario(usuario); n.setLida(false); n.setTitulo("Teste"); n.setMensagem("M"); n.setTipo("SISTEMA");
        when(notificacaoRepository.findByIdAndUsuarioId(3L, 7L)).thenReturn(Optional.of(n));
        when(notificacaoRepository.save(n)).thenReturn(n);

        Optional<NotificacaoDTO> resultado = service.marcarComoLida(3L, 7L);

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isLida());
        verify(notificacaoRepository).save(n);
    }
    @Test
    void criarParaUsuarioEStaffDeveEnviarParaUsuarioEAdmin() {
        Usuario admin = new Usuario();
        admin.setId(9L);
        admin.setTipo("admin");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, admin));
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(inv -> {
            Notificacao n = inv.getArgument(0);
            n.setId(System.nanoTime());
            return n;
        });

        List<NotificacaoDTO> resultado = service.criarParaUsuarioEStaff(usuario, "Titulo", "Mensagem", "EMPRESTIMO");

        assertEquals(2, resultado.size());
        verify(notificacaoRepository, times(2)).save(any(Notificacao.class));
    }

    @Test
    void criarUnicaNaoDeveDuplicarQuandoChaveJaExiste() {
        when(notificacaoRepository.existsByChaveUnica("ATRASO:10")).thenReturn(true);

        Optional<NotificacaoDTO> resultado = service.criarUnica(
                usuario, "Livro em atraso", "Mensagem", "ATRASO", "ATRASO:10");

        assertTrue(resultado.isEmpty());
        verify(notificacaoRepository, never()).save(any(Notificacao.class));
    }

    @Test
    void criarUnicaDeveSalvarChaveQuandoAindaNaoExiste() {
        when(notificacaoRepository.existsByChaveUnica("ATRASO:11")).thenReturn(false);
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(inv -> {
            Notificacao n = inv.getArgument(0);
            n.setId(22L);
            return n;
        });

        Optional<NotificacaoDTO> resultado = service.criarUnica(
                usuario, "Livro em atraso", "Mensagem", "ATRASO", "ATRASO:11");

        assertTrue(resultado.isPresent());
        verify(notificacaoRepository).save(argThat(n -> "ATRASO:11".equals(n.getChaveUnica())));
    }

    @Test
    void criarUnicaParaUsuarioEStaffDeveDeduplicarPorDestinatario() {
        Usuario admin = new Usuario();
        admin.setId(9L);
        admin.setTipo("admin");
        Usuario outroAluno = new Usuario();
        outroAluno.setId(12L);
        outroAluno.setTipo("aluno");
        List<String> chavesSalvas = new java.util.ArrayList<>();

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, admin, outroAluno));
        when(notificacaoRepository.existsByChaveUnica(anyString()))
                .thenAnswer(inv -> chavesSalvas.contains(inv.getArgument(0)));
        when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(inv -> {
            Notificacao notificacao = inv.getArgument(0);
            chavesSalvas.add(notificacao.getChaveUnica());
            notificacao.setId((long) chavesSalvas.size());
            return notificacao;
        });

        List<NotificacaoDTO> primeiraVerificacao = service.criarUnicaParaUsuarioEStaff(
                usuario, "Livro em atraso", "Mensagem", "ATRASO", "EMPRESTIMO_ATRASADO:11");
        List<NotificacaoDTO> segundaVerificacao = service.criarUnicaParaUsuarioEStaff(
                usuario, "Livro em atraso", "Mensagem", "ATRASO", "EMPRESTIMO_ATRASADO:11");

        assertEquals(2, primeiraVerificacao.size());
        assertTrue(segundaVerificacao.isEmpty());
        assertTrue(chavesSalvas.contains("EMPRESTIMO_ATRASADO:11:7"));
        assertTrue(chavesSalvas.contains("EMPRESTIMO_ATRASADO:11:9"));
        verify(notificacaoRepository, times(2)).save(any(Notificacao.class));
    }

}
