package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.Convidado;
import com.domus.tcc.backend.domain.LogConvidado;
import com.domus.tcc.backend.dto.response.DadosConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosLogConvidadoDTO;
import com.domus.tcc.backend.exception.EntradaBloqueadaException;
import com.domus.tcc.backend.repository.ConvidadoRepository;
import com.domus.tcc.backend.repository.LogConvidadoRepository;
import com.domus.tcc.backend.repository.UsuarioRepository;
import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogConvidadoService {

    private final ConvidadoRepository convidadoRepository;
    private final LogConvidadoRepository logConvidadoRepository;
    private final UsuarioRepository usuarioRepository;

    public LogConvidadoService(
        ConvidadoRepository convidadoRepository,
        LogConvidadoRepository logConvidadoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.convidadoRepository = convidadoRepository;
        this.logConvidadoRepository = logConvidadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PreAuthorize("hasRole('PORTEIRO')")
    @Transactional
    public DadosLogConvidadoDTO registrar(Long idConvidado, Usuario logado) {
        Convidado convidado = convidadoRepository.findById(idConvidado)
            .orElseThrow(() -> new EntityNotFoundException("Convidado não encontrado."));

        if (!Boolean.TRUE.equals(convidado.getAtivo())) {
            throw new EntradaBloqueadaException("Entrada bloqueada: convidado inativo.");
        }

        Usuario porteiro = usuarioRepository.findById(logado.getId())
            .orElseThrow(() -> new EntityNotFoundException("Porteiro não encontrado."));

        LogConvidado logConvidado = new LogConvidado(convidado, LocalDateTime.now(), porteiro);
        return new DadosLogConvidadoDTO(logConvidadoRepository.save(logConvidado));
    }

    @PreAuthorize("hasRole('PORTEIRO')")
    @Transactional(readOnly = true)
    public List<DadosLogConvidadoDTO> listar() {
        return logConvidadoRepository.findAll()
            .stream()
            .map(DadosLogConvidadoDTO::new)
            .toList();
    }

    @PreAuthorize("hasRole('PORTEIRO')")
    @Transactional(readOnly = true)
    public List<DadosConvidadoDTO> listarConvidadosPorMorador(Long moradorId) {
        return convidadoRepository.findByMoradorIdAndAtivoTrue(moradorId)
            .stream()
            .map(DadosConvidadoDTO::new)
            .toList();
    }
}
