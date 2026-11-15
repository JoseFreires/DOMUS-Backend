package com.domus.tcc.backend.services;

import com.domus.tcc.backend.domain.Convidado;
import com.domus.tcc.backend.domain.Morador;
import com.domus.tcc.backend.dto.request.DadosAtualizarConvidadoDTO;
import com.domus.tcc.backend.dto.request.DadosCadastrarConvidadoDTO;
import com.domus.tcc.backend.dto.response.DadosConvidadoDTO;
import com.domus.tcc.backend.exception.AcessoNegadoException;
import com.domus.tcc.backend.exception.ConvidadoJaCadastradoException;
import com.domus.tcc.backend.repository.ConvidadoRepository;
import com.domus.tcc.backend.repository.MoradorRepository;
import com.domus.tcc.backend.security.Usuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConvidadoService {

    private final MoradorRepository moradorRepository;
    private final ConvidadoRepository convidadoRepository;

    public ConvidadoService(MoradorRepository moradorRepository, ConvidadoRepository convidadoRepository) {
        this.moradorRepository = moradorRepository;
        this.convidadoRepository = convidadoRepository;
    }

    @PreAuthorize("hasRole('MORADOR')")
    @Transactional
    public DadosConvidadoDTO cadastrar(DadosCadastrarConvidadoDTO dados, Usuario logado) {
        Morador morador = moradorRepository.findByPessoa_Id(logado.getPessoa().getId())
            .orElseThrow(() -> new EntityNotFoundException("Morador não encontrado."));

        if (convidadoRepository.existsByCpfAndMoradorId(dados.cpf(), morador.getId())) {
            throw new ConvidadoJaCadastradoException("CPF já cadastrado para este morador.");
        }

        Convidado convidado = new Convidado(dados, morador);
        return new DadosConvidadoDTO(convidadoRepository.save(convidado));
    }

    @PreAuthorize("hasRole('MORADOR')")
    @Transactional(readOnly = true)
    public List<DadosConvidadoDTO> listar(Usuario logado) {
        Morador morador = moradorRepository.findByPessoa_Id(logado.getPessoa().getId())
            .orElseThrow(() -> new EntityNotFoundException("Morador não encontrado."));

        return convidadoRepository.findByMoradorId(morador.getId())
            .stream()
            .map(DadosConvidadoDTO::new)
            .toList();
    }

    @PreAuthorize("hasRole('MORADOR')")
    @Transactional
    public DadosConvidadoDTO atualizar(Long idConvidado, DadosAtualizarConvidadoDTO dados, Usuario logado) {
        Morador moradorLogado = moradorRepository.findByPessoa_Id(logado.getPessoa().getId())
            .orElseThrow(() -> new EntityNotFoundException("Morador não encontrado."));

        Convidado convidado = convidadoRepository.findById(idConvidado)
            .orElseThrow(() -> new EntityNotFoundException("Convidado não encontrado."));

        if (!convidado.getMorador().getId().equals(moradorLogado.getId())) {
            throw new AcessoNegadoException("Você não pode alterar um convidado de outro morador.");
        }

        if (dados.nome() != null && !dados.nome().isBlank()) {
            convidado.setNome(dados.nome());
        }
        if (dados.telefone() != null && !dados.telefone().isBlank()) {
            convidado.setTelefone(dados.telefone());
        }
        if (dados.recorrencia() != null) {
            convidado.setRecorrencia(dados.recorrencia());
        }

        return new DadosConvidadoDTO(convidadoRepository.save(convidado));
    }

    @PreAuthorize("hasRole('MORADOR')")
    @Transactional
    public DadosConvidadoDTO inativar(Long idConvidado, Usuario logado) {
        Morador moradorLogado = moradorRepository.findByPessoa_Id(logado.getPessoa().getId())
            .orElseThrow(() -> new EntityNotFoundException("Morador não encontrado."));

        Convidado convidado = convidadoRepository.findById(idConvidado)
            .orElseThrow(() -> new EntityNotFoundException("Convidado não encontrado."));

        if (!convidado.getMorador().getId().equals(moradorLogado.getId())) {
            throw new AcessoNegadoException("Você não pode inativar um convidado de outro morador.");
        }

        convidado.setAtivo(false);
        return new DadosConvidadoDTO(convidadoRepository.save(convidado));
    }
}
