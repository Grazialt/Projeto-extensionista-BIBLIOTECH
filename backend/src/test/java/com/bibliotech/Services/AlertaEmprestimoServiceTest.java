package com.bibliotech.Services;

import com.bibliotech.Entidades.Emprestimo;
import com.bibliotech.Entidades.Livro;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaEmprestimoServiceTest {

    @Mock EmprestimoRepository emprestimoRepository;
    @Mock NotificacaoService notificacaoService;

    private AlertaEmprestimoService service;
    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setUp() {
        service = new AlertaEmprestimoService(emprestimoRepository, notificacaoService);
        usuario = new Usuario();
        usuario.setId(7L);
        livro = new Livro();
        livro.setTitulo("Dom Casmurro");
    }

    @Test
    void deveCriarAvisoUnicoQuandoDevolucaoForAmanha() {
        Emprestimo emprestimo = emprestimoAtivo(10L, LocalDate.now().plusDays(1));
        when(emprestimoRepository.findByUsuarioIdAndStatusIgnoreCase(7L, "ativo"))
                .thenReturn(List.of(emprestimo));

        service.verificarUsuario(7L);

        verify(notificacaoService).criarUnica(
                eq(usuario),
                eq("Devolução amanhã"),
                contains("Dom Casmurro"),
                eq("DEVOLUCAO_PROXIMA"),
                eq("DEVOLUCAO_PROXIMA:10:" + emprestimo.getDataDevolucao())
        );
    }

    @Test
    void deveCriarUmAlertaUnicoParaEmprestimoAtrasado() {
        Emprestimo emprestimo = emprestimoAtivo(11L, LocalDate.now().minusDays(2));
        when(emprestimoRepository.findByUsuarioIdAndStatusIgnoreCase(7L, "ativo"))
                .thenReturn(List.of(emprestimo));

        service.verificarUsuario(7L);

        verify(notificacaoService).criarUnica(
                eq(usuario),
                eq("Livro em atraso"),
                contains("venceu em"),
                eq("ATRASO"),
                eq("EMPRESTIMO_ATRASADO:11")
        );
    }

    @Test
    void naoDeveCriarAlertaForaDaJanelaDeAviso() {
        Emprestimo emprestimo = emprestimoAtivo(12L, LocalDate.now().plusDays(5));
        when(emprestimoRepository.findByUsuarioIdAndStatusIgnoreCase(7L, "ativo"))
                .thenReturn(List.of(emprestimo));

        service.verificarUsuario(7L);

        verifyNoInteractions(notificacaoService);
    }

    private Emprestimo emprestimoAtivo(Long id, LocalDate devolucao) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(id);
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setStatus("ativo");
        emprestimo.setDataDevolucao(devolucao);
        return emprestimo;
    }
}
