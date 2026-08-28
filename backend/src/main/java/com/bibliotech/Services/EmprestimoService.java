package com.bibliotech.Services;

import com.bibliotech.DTOs.EmprestimoDTO;
import com.bibliotech.Entidades.Emprestimo;
import com.bibliotech.Entidades.Livro;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.EmprestimoRepository;
import com.bibliotech.Repositories.LivroRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    public EmprestimoDTO criarEmprestimo(Long livroId, Long usuarioId, LocalDate dataDevolucao) {
        Optional<Livro> livro = livroRepository.findById(livroId);
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (livro.isEmpty() || usuario.isEmpty()) {
            return null;
        }

        if (livro.get().getQuantidade() <= 0) {
            return null;
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro.get());
        emprestimo.setUsuario(usuario.get());
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(dataDevolucao != null ? dataDevolucao : LocalDate.now().plusDays(14));
        emprestimo.setStatus("ativo");

        Livro livroAtualizado = livro.get();
        livroAtualizado.setQuantidade(livroAtualizado.getQuantidade() - 1);
        livroRepository.save(livroAtualizado);

        Emprestimo emprestimoCriado = emprestimoRepository.save(emprestimo);

        notificacaoService.criar(
                usuario.get(),
                "Empréstimo realizado",
                "O livro \"" + livro.get().getTitulo() + "\" foi emprestado. Devolução prevista para " + emprestimoCriado.getDataDevolucao() + ".",
                "EMPRESTIMO"
        );

        return converterParaDTO(emprestimoCriado);
    }

    public List<EmprestimoDTO> listarEmprestimosPorUsuario(Long usuarioId) {
        return emprestimoRepository.findAll().stream()
            .filter(e -> e.getUsuario().getId().equals(usuarioId))
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    public List<EmprestimoDTO> listarPorStatus(String status) {
        return emprestimoRepository.findAll().stream()
                .filter(e -> e.getStatus() != null && e.getStatus().equalsIgnoreCase(status))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<EmprestimoDTO> listarAtrasados() {
        LocalDate hoje = LocalDate.now();
        return emprestimoRepository.findAll().stream()
                .filter(e -> "ativo".equalsIgnoreCase(e.getStatus()) && e.getDataDevolucao() != null && e.getDataDevolucao().isBefore(hoje))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public boolean devolverEmprestimo(Long emprestimoId) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(emprestimoId);

        if (emprestimo.isEmpty() || !emprestimo.get().getStatus().equalsIgnoreCase("ativo")) {
            return false;
        }

        Emprestimo emp = emprestimo.get();
        emp.setStatus("devolvido");
        emp.setDataDevolucao(LocalDate.now());

        Livro livro = emp.getLivro();
        livro.setQuantidade(livro.getQuantidade() + 1);
        livroRepository.save(livro);

        emprestimoRepository.save(emp);

        notificacaoService.criar(
                emp.getUsuario(),
                "Livro devolvido",
                "A devolução do livro \"" + emp.getLivro().getTitulo() + "\" foi registrada com sucesso.",
                "DEVOLUCAO"
        );
        return true;
    }

    public Optional<EmprestimoDTO> obterEmprestimoPorId(Long id) {
        return emprestimoRepository.findById(id).map(this::converterParaDTO);
    }

    public List<EmprestimoDTO> listarTodos() {
        return emprestimoRepository.findAll().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    private EmprestimoDTO converterParaDTO(Emprestimo emprestimo) {
        EmprestimoDTO dto = new EmprestimoDTO(
            emprestimo.getId(),
            emprestimo.getLivro().getId(),
            emprestimo.getUsuario().getId(),
            emprestimo.getDataEmprestimo(),
            emprestimo.getDataDevolucao(),
            emprestimo.getStatus()
        );
        dto.setLivroTitulo(emprestimo.getLivro().getTitulo());
        dto.setUsuarioNome(emprestimo.getUsuario().getNome());
        return dto;
    }
}
