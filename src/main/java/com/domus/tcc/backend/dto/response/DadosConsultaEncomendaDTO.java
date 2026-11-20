package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.Encomenda;
import com.domus.tcc.backend.domain.enums.TipoRetirada;

import java.time.LocalDateTime;
import java.util.List;

public record DadosConsultaEncomendaDTO(
        Long idEncomenda,
        String nomePacote,
        String observacao,
        String status,
        String nomeMorador,    // Antes: nomeDestinatario
        String nomePorteiroRecebedor, // Antes: turnoPorteiro (não existe mais turno na tabela usuario)
        List<DadosConsultaMoradorMoradiaDTO> moradias,
        String emailDestinatario,
        String tokenEncomenda,
        LocalDateTime dataHoraRecebido,
        LocalDateTime dataHoraRetirado,
        Long idDestinatario,
        TipoRetirada tipoRetirada,
        String fotoPacote,
        String nomeRecebedor,
        String NomeporteiroRemetente
){
    // Construtor para converter a Entidade Encomenda direto para o DTO
    public DadosConsultaEncomendaDTO(Encomenda encomenda) {
        this(
            encomenda.getIdEncomenda(),
            encomenda.getNomePacote(),
            encomenda.getObservacao(),
            encomenda.getStatusEncomenda().toString(),
            encomenda.getMoradorDestinatario().getNomeCompleto(), // Pega o nome do Usuario (Morador)
            encomenda.getPorteiro().getPessoa().getNomeCompleto(),// Pega o nome do Usuario (Porteiro)
            encomenda.getMoradorDestinatario().getMorador() == null
                    ? List.of()
                    : encomenda.getMoradorDestinatario().getMorador().getMoradias().stream()
                        .map(DadosConsultaMoradorMoradiaDTO::new)
                        .toList(),
            encomenda.getMoradorDestinatario().getEmail(),
            encomenda.getToken(),
            encomenda.getDataHoraRecebido(),
            encomenda.getDataHoraRetirado(),
            encomenda.getMoradorDestinatario().getId(),
            encomenda.getTipoRetirada(),
            encomenda.getFotoEncomenda(),
            encomenda.getNomeRecebedor(),
            encomenda.getPorteiroRemetente() != null
                    ? encomenda.getPorteiroRemetente().getPessoa().getNomeCompleto()
                    : null
        );
    }
}