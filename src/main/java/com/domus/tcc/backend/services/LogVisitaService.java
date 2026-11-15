package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.LogVisita;
import com.domus.tcc.backend.domain.Moradia;
import com.domus.tcc.backend.dto.request.DadosRegistrarLogVisitaDTO;
import com.domus.tcc.backend.dto.response.DadosLogVisitaDTO;
import com.domus.tcc.backend.repository.MoradiaRepository;
import com.domus.tcc.backend.repository.LogVisitaRepository;
import com.domus.tcc.backend.repository.UsuarioRepository;
import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogVisitaService {

    private final MoradiaRepository moradiaRepository;
    private final LogVisitaRepository logVisitaRepository;
    private final UsuarioRepository usuarioRepository;

    public LogVisitaService(
        MoradiaRepository moradiaRepository,
        LogVisitaRepository logVisitaRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.moradiaRepository = moradiaRepository;
        this.logVisitaRepository = logVisitaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PreAuthorize("hasRole('PORTEIRO')")
    @Transactional
    public DadosLogVisitaDTO registrar(DadosRegistrarLogVisitaDTO dados, Usuario logado) {
        Moradia moradia = moradiaRepository.findById(dados.idMoradia())
            .orElseThrow(() -> new EntityNotFoundException("Moradia não encontrada."));

        Usuario porteiro = usuarioRepository.findById(logado.getId())
            .orElseThrow(() -> new EntityNotFoundException("Porteiro não encontrado."));

        LogVisita logVisita = new LogVisita(
            dados.nome(),
            dados.tipoVisita(),
            LocalDateTime.now(),
            moradia,
            porteiro
        );

        return new DadosLogVisitaDTO(logVisitaRepository.save(logVisita));
    }

    @PreAuthorize("hasRole('PORTEIRO')")
    @Transactional(readOnly = true)
    public List<DadosLogVisitaDTO> listar() {
        return logVisitaRepository.findAll()
            .stream()
            .map(DadosLogVisitaDTO::new)
            .toList();
    }
}
