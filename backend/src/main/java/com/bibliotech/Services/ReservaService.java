package com.bibliotech.Services;

import com.bibliotech.DTOs.ReservaDTO;
import com.bibliotech.Entidades.Reserva;
import com.bibliotech.Entidades.Livro;
import com.bibliotech.Entidades.Usuario;
import com.bibliotech.Repositories.ReservaRepository;
import com.bibliotech.Repositories.LivroRepository;
import com.bibliotech.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Criar nova reserva
     */
    public ReservaDTO criarReserva(Long livroId, Long usuarioId) {
        Optional<Livro> livro = livroRepository.findById(livroId);
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (livro.isEmpty() || usuario.isEmpty()) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setLivro(livro.get());
        reserva.setUsuario(usuario.get());
        reserva.setDataReserva(LocalDate.now());
        reserva.setStatus("ativa");

        Reserva reservaCriada = reservaRepository.save(reserva);

        notificacaoService.criar(
                usuario.get(),
                "Reserva realizada",
                "O livro \"" + livro.get().getTitulo() + "\" foi reservado com sucesso.",
                "RESERVA"
        );
        return converterParaDTO(reservaCriada);
    }

    /**
     * Listar reservas do usuário
     */
    public List<ReservaDTO> listarReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findAll().stream()
            .filter(r -> r.getUsuario().getId().equals(usuarioId))
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    /**
     * Cancelar reserva
     */
    public boolean cancelarReserva(Long reservaId) {
        Optional<Reserva> reserva = reservaRepository.findById(reservaId);

        if (reserva.isEmpty() || !reserva.get().getStatus().equals("ativa")) {
            return false;
        }

        Reserva res = reserva.get();
        res.setStatus("cancelada");
        reservaRepository.save(res);

        notificacaoService.criar(
                res.getUsuario(),
                "Reserva cancelada",
                "A reserva do livro \"" + res.getLivro().getTitulo() + "\" foi cancelada.",
                "RESERVA"
        );
        return true;
    }

    /**
     * Obter reserva pelo ID
     */
    public Optional<ReservaDTO> obterReservaPorId(Long id) {
        return reservaRepository.findById(id).map(this::converterParaDTO);
    }

    /**
     * Listar todas as reservas
     */
    public List<ReservaDTO> listarTodas() {
        return reservaRepository.findAll().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    private ReservaDTO converterParaDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO(
            reserva.getId(),
            reserva.getLivro().getId(),
            reserva.getUsuario().getId(),
            reserva.getDataReserva(),
            reserva.getStatus()
        );
        dto.setLivroTitulo(reserva.getLivro().getTitulo());
        dto.setUsuarioNome(reserva.getUsuario().getNome());
        return dto;
    }
}
