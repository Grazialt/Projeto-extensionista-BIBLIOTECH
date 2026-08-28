package com.bibliotech.Services;

import com.bibliotech.Entidades.Emprestimo;
import com.bibliotech.Repositories.EmprestimoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AlertaEmprestimoService {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmprestimoRepository emprestimoRepository;
    private final NotificacaoService notificacaoService;

    public AlertaEmprestimoService(
            EmprestimoRepository emprestimoRepository,
            NotificacaoService notificacaoService) {
        this.emprestimoRepository = emprestimoRepository;
        this.notificacaoService = notificacaoService;
    }

    public void verificarUsuario(Long usuarioId) {
        if (usuarioId == null) return;
        verificarEmprestimos(emprestimoRepository.findByUsuarioIdAndStatusIgnoreCase(usuarioId, "ativo"));
    }

    @Scheduled(fixedDelay = 3_600_000, initialDelay = 30_000)
    public void verificarTodos() {
        verificarEmprestimos(emprestimoRepository.findByStatusIgnoreCase("ativo"));
    }

    private void verificarEmprestimos(List<Emprestimo> emprestimos) {
        LocalDate hoje = LocalDate.now();

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getDataDevolucao() == null
                    || emprestimo.getUsuario() == null
                    || emprestimo.getLivro() == null) {
                continue;
            }

            LocalDate dataDevolucao = emprestimo.getDataDevolucao();
            String tituloLivro = emprestimo.getLivro().getTitulo();

            if (dataDevolucao.equals(hoje.plusDays(1))) {
                notificacaoService.criarUnica(
                        emprestimo.getUsuario(),
                        "Devolução amanhã",
                        "O prazo para devolver \"" + tituloLivro + "\" termina amanhã ("
                                + dataDevolucao.format(DATA_BR) + ").",
                        "DEVOLUCAO_PROXIMA",
                        "DEVOLUCAO_PROXIMA:" + emprestimo.getId() + ":" + dataDevolucao
                );
            } else if (dataDevolucao.isBefore(hoje)) {
                notificacaoService.criarUnica(
                        emprestimo.getUsuario(),
                        "Livro em atraso",
                        "O prazo para devolver \"" + tituloLivro + "\" venceu em "
                                + dataDevolucao.format(DATA_BR) + ". Faça a devolução o quanto antes.",
                        "ATRASO",
                        "EMPRESTIMO_ATRASADO:" + emprestimo.getId()
                );
            }
        }
    }
}
