package com.bibliotech.DTOs;

import java.util.List;

/**
 * Relatório detalhado de uma turma específica.
 */
public class DetalheTurmaDTO {

    private String turma;
    private long totalAlunos;
    private long alunosQueLeram;        // ao menos 1 livro devolvido
    private long alunosSemLeitura;      // nenhum livro devolvido
    private long totalLivrosLidos;
    private double mediaLivrosPorAluno;
    private long emprestimosAtivos;
    private long emprestimosAtrasados;

    /** Alunos da turma ordenados por livros lidos (desc) */
    private List<AlunoTurmaDTO> rankingAlunos;

    /** Alunos cadastrados na turma que nunca devolveram nenhum livro */
    private List<String> alunosSemLeituraLista;

    /** Livros mais emprestados dentro da turma */
    private List<RankingDTO> livrosMaisLidos;

    /** Evolução mensal de empréstimos nos últimos 6 meses, apenas desta turma */
    private List<EvolucaoMensalDTO> evolucaoMensal;

    public DetalheTurmaDTO() {}

    // ── getters e setters ──────────────────────────────────────────────────────

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public long getTotalAlunos() { return totalAlunos; }
    public void setTotalAlunos(long totalAlunos) { this.totalAlunos = totalAlunos; }

    public long getAlunosQueLeram() { return alunosQueLeram; }
    public void setAlunosQueLeram(long alunosQueLeram) { this.alunosQueLeram = alunosQueLeram; }

    public long getAlunosSemLeitura() { return alunosSemLeitura; }
    public void setAlunosSemLeitura(long alunosSemLeitura) { this.alunosSemLeitura = alunosSemLeitura; }

    public long getTotalLivrosLidos() { return totalLivrosLidos; }
    public void setTotalLivrosLidos(long totalLivrosLidos) { this.totalLivrosLidos = totalLivrosLidos; }

    public double getMediaLivrosPorAluno() { return mediaLivrosPorAluno; }
    public void setMediaLivrosPorAluno(double mediaLivrosPorAluno) { this.mediaLivrosPorAluno = mediaLivrosPorAluno; }

    public long getEmprestimosAtivos() { return emprestimosAtivos; }
    public void setEmprestimosAtivos(long emprestimosAtivos) { this.emprestimosAtivos = emprestimosAtivos; }

    public long getEmprestimosAtrasados() { return emprestimosAtrasados; }
    public void setEmprestimosAtrasados(long emprestimosAtrasados) { this.emprestimosAtrasados = emprestimosAtrasados; }

    public List<AlunoTurmaDTO> getRankingAlunos() { return rankingAlunos; }
    public void setRankingAlunos(List<AlunoTurmaDTO> rankingAlunos) { this.rankingAlunos = rankingAlunos; }

    public List<String> getAlunosSemLeituraLista() { return alunosSemLeituraLista; }
    public void setAlunosSemLeituraLista(List<String> alunosSemLeituraLista) { this.alunosSemLeituraLista = alunosSemLeituraLista; }

    public List<RankingDTO> getLivrosMaisLidos() { return livrosMaisLidos; }
    public void setLivrosMaisLidos(List<RankingDTO> livrosMaisLidos) { this.livrosMaisLidos = livrosMaisLidos; }

    public List<EvolucaoMensalDTO> getEvolucaoMensal() { return evolucaoMensal; }
    public void setEvolucaoMensal(List<EvolucaoMensalDTO> evolucaoMensal) { this.evolucaoMensal = evolucaoMensal; }

    // ── DTO interno: aluno dentro do ranking da turma ──────────────────────────
    public static class AlunoTurmaDTO {
        private String nome;
        private String matricula;
        private long livrosLidos;
        private long emprestimosAtivos;

        public AlunoTurmaDTO() {}

        public AlunoTurmaDTO(String nome, String matricula, long livrosLidos, long emprestimosAtivos) {
            this.nome = nome;
            this.matricula = matricula;
            this.livrosLidos = livrosLidos;
            this.emprestimosAtivos = emprestimosAtivos;
        }

        public String getNome() { return nome; }
        public String getMatricula() { return matricula; }
        public long getLivrosLidos() { return livrosLidos; }
        public long getEmprestimosAtivos() { return emprestimosAtivos; }
    }
}
