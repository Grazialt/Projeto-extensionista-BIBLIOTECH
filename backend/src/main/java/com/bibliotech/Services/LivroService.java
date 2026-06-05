package com.bibliotech.Services;

import com.bibliotech.DTOs.LivroDTO;
import com.bibliotech.Entidades.Autor;
import com.bibliotech.Entidades.Categoria;
import com.bibliotech.Entidades.Livro;
import com.bibliotech.Repositories.AutorRepository;
import com.bibliotech.Repositories.CategoriaRepository;
import com.bibliotech.Repositories.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<LivroDTO> listarTodos() {
        return livroRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public Optional<LivroDTO> obterPorId(Long id) {
        return livroRepository.findById(id).map(this::converterParaDTO);
    }

    public List<LivroDTO> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        String termoMinusculo = termo.toLowerCase();
        return livroRepository.findAll().stream()
                .filter(l -> (l.getTitulo() != null && l.getTitulo().toLowerCase().contains(termoMinusculo))
                        || (l.getIsbn() != null && l.getIsbn().toLowerCase().contains(termoMinusculo))
                        || (l.getAutor() != null && l.getAutor().getNome() != null && l.getAutor().getNome().toLowerCase().contains(termoMinusculo)))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public LivroDTO criar(LivroDTO dto) {
        Livro livro = converterParaEntidade(dto);
        Livro salvo = livroRepository.save(livro);
        return converterParaDTO(salvo);
    }

    public Optional<LivroDTO> atualizar(Long id, LivroDTO dto) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return Optional.empty();
        }
        Livro livro = livroOpt.get();
        livro.setTitulo(dto.getTitulo());
        livro.setIsbn(dto.getIsbn());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setQuantidade(dto.getQuantidade());
        livro.setEditora(dto.getEditora());
        livro.setEdicao(dto.getEdicao());
        livro.setPaginas(dto.getPaginas());

        if (dto.getAutorId() != null) {
            Optional<Autor> autorOpt = autorRepository.findById(dto.getAutorId());
            autorOpt.ifPresent(livro::setAutor);
        }
        if (dto.getCategoriaId() != null) {
            Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
            categoriaOpt.ifPresent(livro::setCategoria);
        }

        Livro atualizado = livroRepository.save(livro);
        return Optional.of(converterParaDTO(atualizado));
    }

    public boolean deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            return false;
        }
        livroRepository.deleteById(id);
        return true;
    }

    private LivroDTO converterParaDTO(Livro livro) {
        LivroDTO dto = new LivroDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getQuantidade()
        );
        dto.setEditora(livro.getEditora());
        dto.setEdicao(livro.getEdicao());
        dto.setPaginas(livro.getPaginas());
        if (livro.getAutor() != null) {
            dto.setAutorId(livro.getAutor().getId());
            dto.setAutorNome(livro.getAutor().getNome());
        }
        if (livro.getCategoria() != null) {
            dto.setCategoriaId(livro.getCategoria().getId());
            dto.setCategoriaNome(livro.getCategoria().getNome());
        }
        return dto;
    }

    private Livro converterParaEntidade(LivroDTO dto) {
        Livro livro = new Livro();
        livro.setTitulo(dto.getTitulo());
        livro.setIsbn(dto.getIsbn());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setQuantidade(dto.getQuantidade());
        livro.setEditora(dto.getEditora());
        livro.setEdicao(dto.getEdicao());
        livro.setPaginas(dto.getPaginas());

        if (dto.getAutorId() != null) {
            Optional<Autor> autorOpt = autorRepository.findById(dto.getAutorId());
            autorOpt.ifPresent(livro::setAutor);
        } else if (dto.getAutorNome() != null && !dto.getAutorNome().isBlank()) {
            String nomeAutor = dto.getAutorNome().trim();
            Optional<Autor> autorOpt = autorRepository.findByNome(nomeAutor);
            Autor autor = autorOpt.orElseGet(() -> {
                Autor novoAutor = new Autor();
                novoAutor.setNome(nomeAutor);
                return autorRepository.save(novoAutor);
            });
            livro.setAutor(autor);
        }

        if (dto.getCategoriaId() != null) {
            Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoriaId());
            categoriaOpt.ifPresent(livro::setCategoria);
        } else if (dto.getCategoriaNome() != null && !dto.getCategoriaNome().isBlank()) {
            String nomeCategoria = dto.getCategoriaNome().trim();
            Optional<Categoria> categoriaOpt = categoriaRepository.findByNome(nomeCategoria);
            Categoria categoria = categoriaOpt.orElseGet(() -> {
                Categoria novaCategoria = new Categoria();
                novaCategoria.setNome(nomeCategoria);
                return categoriaRepository.save(novaCategoria);
            });
            livro.setCategoria(categoria);
        }
        return livro;
    }
}
