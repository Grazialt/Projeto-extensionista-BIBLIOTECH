package com.bibliotech.Services;

import com.bibliotech.DTOs.DetalheTurmaDTO;
import com.bibliotech.DTOs.EvolucaoMensalDTO;
import com.bibliotech.DTOs.IndicadorDTO;
import com.bibliotech.DTOs.RankingDTO;
import com.bibliotech.DTOs.RankingTurmaDTO;
import com.bibliotech.Entidades.Emprestimo;
import com.bibliotech.Entidades.Livro;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.EmprestimoRepository;
import com.bibliotech.Repositories.LivroRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class RelatorioService {

    @Autowired private LivroRepository livroRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;

    public IndicadorDTO indicadores() {
        List<Livro> livros = livroRepository.findAll();
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        long totalLivros = livros.size();
        long disponiveis = livros.stream().map(Livro::getQuantidade)
                .filter(Objects::nonNull).mapToLong(Integer::longValue).sum();
        long usuarios = usuarioRepository.countAlunos();
        long ativos = emprestimos.stream().filter(this::ativo).count();
        long atrasados = emprestimos.stream()
                .filter(e -> ativo(e) && e.getDataPrevistaDevolucao() != null
                        && e.getDataPrevistaDevolucao().isBefore(LocalDate.now())).count();
        long devolvidos = emprestimos.stream().filter(this::devolvido).count();
        long total = emprestimos.size();
        double percentual = total == 0 ? 0 : (devolvidos * 100.0 / total);

        double media = usuarios == 0 ? 0 : (devolvidos * 1.0 / usuarios);

        return new IndicadorDTO(totalLivros, disponiveis, usuarios, total, ativos, atrasados,
                devolvidos, arredondar(percentual), arredondar(media));
    }

    public List<RankingDTO> rankingAlunos() {
        return emprestimoRepository.findAll().stream()
                .filter(this::devolvido)
                .filter(e -> e.getUsuario() != null)
                .collect(Collectors.groupingBy(e -> e.getUsuario().getNome(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new RankingDTO(e.getKey(), null, e.getValue()))
                .toList();
    }

    public List<RankingDTO> livrosMaisEmprestados() {
        return emprestimoRepository.findAll().stream()
                .filter(e -> e.getLivro() != null)
                .collect(Collectors.groupingBy(e -> e.getLivro().getTitulo(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new RankingDTO(null, e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Agrupa empréstimos devolvidos por turma, ordenando da turma que mais leu
     * para a que menos leu. Empréstimos de alunos sem turma são ignorados.
     */
    public List<RankingTurmaDTO> rankingTurmas() {
        List<Emprestimo> todos = emprestimoRepository.findAll();

        // livros lidos (devolvidos) por turma
        Map<String, Long> livrosPorTurma = todos.stream()
                .filter(this::devolvido)
                .filter(e -> e.getUsuario() != null && e.getUsuario().getTurma() != null
                        && !e.getUsuario().getTurma().isBlank())
                .collect(Collectors.groupingBy(e -> e.getUsuario().getTurma(), Collectors.counting()));

        // alunos distintos com pelo menos 1 empréstimo ativo por turma
        Map<String, Long> alunosPorTurma = todos.stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getTurma() != null
                        && !e.getUsuario().getTurma().isBlank())
                .collect(Collectors.groupingBy(
                        e -> e.getUsuario().getTurma(),
                        Collectors.mapping(e -> e.getUsuario().getId(), Collectors.toSet())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

        // une os dois mapas e ordena por livros lidos desc
        return livrosPorTurma.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new RankingTurmaDTO(
                        e.getKey(),
                        e.getValue(),
                        alunosPorTurma.getOrDefault(e.getKey(), 0L)))
                .toList();
    }

    /** Retorna todas as turmas distintas cadastradas (alunos com turma preenchida). */
    public List<String> listaTurmas() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getTurma() != null && !u.getTurma().isBlank()
                        && !"bibliotecario".equalsIgnoreCase(u.getTipo())
                        && !"admin".equalsIgnoreCase(u.getTipo()))
                .map(Usuario::getTurma)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Relatório detalhado de uma turma: ranking de alunos, alunos sem leitura,
     * livros mais lidos na turma e evolução mensal.
     */
    public DetalheTurmaDTO detalheTurma(String turma) {
        List<Emprestimo> todos = emprestimoRepository.findAll();
        List<Usuario> alunosDaTurma = usuarioRepository.findAll().stream()
                .filter(u -> turma.equalsIgnoreCase(u.getTurma())
                        && !"bibliotecario".equalsIgnoreCase(u.getTipo())
                        && !"admin".equalsIgnoreCase(u.getTipo()))
                .toList();

        Set<Long> idsAlunos = alunosDaTurma.stream().map(Usuario::getId).collect(Collectors.toSet());

        List<Emprestimo> emprestimosATurma = todos.stream()
                .filter(e -> e.getUsuario() != null && idsAlunos.contains(e.getUsuario().getId()))
                .toList();

        // ── indicadores gerais da turma ──────────────────────────────────────
        long totalAlunos = alunosDaTurma.size();
        long totalLivrosLidos = emprestimosATurma.stream().filter(this::devolvido).count();

        Set<Long> idsQueLeram = emprestimosATurma.stream()
                .filter(this::devolvido)
                .map(e -> e.getUsuario().getId())
                .collect(Collectors.toSet());
        long alunosQueLeram = idsQueLeram.size();
        long alunosSemLeitura = totalAlunos - alunosQueLeram;

        double media = totalAlunos == 0 ? 0 : arredondar(totalLivrosLidos * 1.0 / totalAlunos);

        long emprestimosAtivos = emprestimosATurma.stream().filter(this::ativo).count();
        long emprestimosAtrasados = emprestimosATurma.stream()
                .filter(e -> ativo(e) && e.getDataPrevistaDevolucao() != null
                        && e.getDataPrevistaDevolucao().isBefore(LocalDate.now()))
                .count();

        // ── ranking de alunos da turma ───────────────────────────────────────
        Map<Long, Long> livrosPorAluno = emprestimosATurma.stream()
                .filter(this::devolvido)
                .collect(Collectors.groupingBy(e -> e.getUsuario().getId(), Collectors.counting()));

        Map<Long, Long> ativosPorAluno = emprestimosATurma.stream()
                .filter(this::ativo)
                .collect(Collectors.groupingBy(e -> e.getUsuario().getId(), Collectors.counting()));

        List<DetalheTurmaDTO.AlunoTurmaDTO> rankingAlunos = alunosDaTurma.stream()
                .sorted((a, b) -> Long.compare(
                        livrosPorAluno.getOrDefault(b.getId(), 0L),
                        livrosPorAluno.getOrDefault(a.getId(), 0L)))
                .map(u -> new DetalheTurmaDTO.AlunoTurmaDTO(
                        u.getNome(),
                        u.getMatricula(),
                        livrosPorAluno.getOrDefault(u.getId(), 0L),
                        ativosPorAluno.getOrDefault(u.getId(), 0L)))
                .toList();

        // ── alunos da turma que nunca leram ──────────────────────────────────
        List<String> semLeituraLista = alunosDaTurma.stream()
                .filter(u -> !idsQueLeram.contains(u.getId()))
                .map(Usuario::getNome)
                .sorted()
                .toList();

        // ── livros mais lidos na turma ───────────────────────────────────────
        List<RankingDTO> livrosMaisLidos = emprestimosATurma.stream()
                .filter(this::devolvido)
                .filter(e -> e.getLivro() != null)
                .collect(Collectors.groupingBy(e -> e.getLivro().getTitulo(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new RankingDTO(null, e.getKey(), e.getValue()))
                .toList();

        // ── evolução mensal da turma (últimos 6 meses) ───────────────────────
        Map<YearMonth, Long> porMes = emprestimosATurma.stream()
                .filter(e -> e.getDataEmprestimo() != null)
                .collect(Collectors.groupingBy(e -> YearMonth.from(e.getDataEmprestimo()), Collectors.counting()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth atual = YearMonth.now();
        List<EvolucaoMensalDTO> evolucao = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = atual.minusMonths(i);
            evolucao.add(new EvolucaoMensalDTO(mes.format(formatter), porMes.getOrDefault(mes, 0L)));
        }

        // ── monta o DTO final ────────────────────────────────────────────────
        DetalheTurmaDTO dto = new DetalheTurmaDTO();
        dto.setTurma(turma);
        dto.setTotalAlunos(totalAlunos);
        dto.setAlunosQueLeram(alunosQueLeram);
        dto.setAlunosSemLeitura(alunosSemLeitura);
        dto.setTotalLivrosLidos(totalLivrosLidos);
        dto.setMediaLivrosPorAluno(media);
        dto.setEmprestimosAtivos(emprestimosAtivos);
        dto.setEmprestimosAtrasados(emprestimosAtrasados);
        dto.setRankingAlunos(rankingAlunos);
        dto.setAlunosSemLeituraLista(semLeituraLista);
        dto.setLivrosMaisLidos(livrosMaisLidos);
        dto.setEvolucaoMensal(evolucao);
        return dto;
    }

    public List<EvolucaoMensalDTO> evolucaoMensal() {
        Map<YearMonth, Long> porMes = emprestimoRepository.findAll().stream()
                .filter(e -> e.getDataEmprestimo() != null)
                .collect(Collectors.groupingBy(e -> YearMonth.from(e.getDataEmprestimo()), Collectors.counting()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth atual = YearMonth.now();
        List<EvolucaoMensalDTO> resultado = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth mes = atual.minusMonths(i);
            resultado.add(new EvolucaoMensalDTO(mes.format(formatter), porMes.getOrDefault(mes, 0L)));
        }
        return resultado;
    }

    private boolean ativo(Emprestimo e) {
        return e.getStatus() != null && "ativo".equalsIgnoreCase(e.getStatus());
    }

    private boolean devolvido(Emprestimo e) {
        return e.getStatus() != null && "devolvido".equalsIgnoreCase(e.getStatus());
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
