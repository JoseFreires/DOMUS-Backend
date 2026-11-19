package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.LogVisita;
import com.domus.tcc.backend.domain.enums.TipoVisita;

import java.time.LocalDateTime;

public record DadosLogVisitaDTO(
    Long idLogVisita,
    String nome,
    TipoVisita tipoVisita,
    String numeroMoradia,
    String nomeTorre,
    String nomePorteiro,
    LocalDateTime dataHoraChegada
) {
    public DadosLogVisitaDTO(LogVisita logVisita) {
        this(
            logVisita.getIdLogVisita(),
            logVisita.getNome(),
            logVisita.getTipoVisita(),
            logVisita.getMoradia().getNumero(),
            logVisita.getMoradia().getBloco().getNome_torre(),
            logVisita.getPorteiro().getPessoa().getNomeCompleto(),
            logVisita.getDataHoraChegada()
        );
    }
}
