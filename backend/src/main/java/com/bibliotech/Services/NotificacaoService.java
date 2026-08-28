package com.bibliotech.Services;

import com.bibliotech.DTOs.NotificacaoDTO;
import com.bibliotech.Entidades.Notificacao;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.NotificacaoRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public NotificacaoDTO criar(Usuario usuario, String titulo, String mensagem, String tipo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());
        return converterParaDTO(notificacaoRepository.save(notificacao));
    }

    public Optional<NotificacaoDTO> criar(Long usuarioId, String titulo, String mensagem, String tipo) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> criar(usuario, titulo, mensagem, tipo));
    }

    public synchronized Optional<NotificacaoDTO> criarUnica(
            Usuario usuario,
            String titulo,
            String mensagem,
            String tipo,
            String chaveUnica) {
        if (chaveUnica == null || chaveUnica.isBlank()) {
            return Optional.of(criar(usuario, titulo, mensagem, tipo));
        }

        if (notificacaoRepository.existsByChaveUnica(chaveUnica)) {
            return Optional.empty();
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setChaveUnica(chaveUnica);
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());
        return Optional.of(converterParaDTO(notificacaoRepository.save(notificacao)));
    }

    public List<NotificacaoDTO> listarPorUsuario(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId)
                .stream().map(this::converterParaDTO).toList();
    }

    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    public Optional<NotificacaoDTO> marcarComoLida(Long notificacaoId, Long usuarioId) {
        return notificacaoRepository.findByIdAndUsuarioId(notificacaoId, usuarioId)
                .map(notificacao -> {
                    notificacao.setLida(true);
                    return converterParaDTO(notificacaoRepository.save(notificacao));
                });
    }

    public int marcarTodasComoLidas(Long usuarioId) {
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId);
        naoLidas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(naoLidas);
        return naoLidas.size();
    }

    private NotificacaoDTO converterParaDTO(Notificacao notificacao) {
        return new NotificacaoDTO(
                notificacao.getId(),
                notificacao.getUsuario().getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getTipo(),
                notificacao.isLida(),
                notificacao.getDataCriacao()
        );
    }
}
