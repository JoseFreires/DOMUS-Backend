package com.domus.tcc.backend.dto.request;

import com.domus.tcc.backend.domain.enums.Recorrencia;

public record DadosAtualizarConvidadoDTO(
    String nome,
    String telefone,
    Recorrencia recorrencia
) {
}
