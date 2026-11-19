package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.LogConvidado;

import java.time.LocalDateTime;

public record DadosLogConvidadoDTO(
    Long idLogConvidado,
    String nomeConvidado,
    String nomePorteiro,
    LocalDateTime dataHoraChegada
) {
    public DadosLogConvidadoDTO(LogConvidado logConvidado) {
        this(
            logConvidado.getIdLogConvidado(),
            logConvidado.getConvidado().getNome(),
            logConvidado.getPorteiro().getPessoa().getNomeCompleto(),
            logConvidado.getDataHoraChegada()
        );
    }
}
