package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.Moradia;

public record DadosConsultaMoradorMoradiaDTO(
        Long idMoradia,
        Long idBloco,
        String numeroApartamento,
        String bloco
) {
    public DadosConsultaMoradorMoradiaDTO(Moradia moradia) {
        this(
                moradia.getId(),
                moradia.getBloco().getId(),
                moradia.getNumero(),
                moradia.getBloco().getNome_torre()
        );
    }
}
