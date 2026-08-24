package com.bibliotech.Services;

import com.bibliotech.DTOs.EvolucaoMensalDTO;
import com.bibliotech.DTOs.IndicadorDTO;
import com.bibliotech.DTOs.RankingDTO;
import com.bibliotech.Entidades.Emprestimo;
import com.bibliotech.Entidades.Livro;
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
        long usuarios = usuarioRepository.count();
        long ativos = emprestimos.stream().filter(this::ativo).count();
        long atrasados = emprestimos.stream()
                .filter(e -> ativo(e) && e.getDataDevolucao() != null
                        && e.getDataDevolucao().isBefore(LocalDate.now())).count();
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
