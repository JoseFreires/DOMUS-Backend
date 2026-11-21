package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.LogConvidado;
import com.domus.tcc.backend.domain.enums.Recorrencia;

import java.time.LocalDateTime;

public record DadosLogConvidadoDTO(
    Long idLogConvidado,
    String nomeConvidado,
    String nomePorteiro,
    LocalDateTime dataHoraChegada,
    String nomeMorador,
    Recorrencia recorrencia
) {
    public DadosLogConvidadoDTO(LogConvidado logConvidado) {
        this(
            logConvidado.getIdLogConvidado(),
            logConvidado.getConvidado().getNome(),
            logConvidado.getPorteiro().getPessoa().getNomeCompleto(),
            logConvidado.getDataHoraChegada(),
            logConvidado.getConvidado().getMorador().getPessoa().getNomeCompleto(),
            logConvidado.getConvidado().getRecorrencia()
        );
    }
}
