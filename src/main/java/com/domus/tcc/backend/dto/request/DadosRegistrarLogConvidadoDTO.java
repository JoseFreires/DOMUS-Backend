package com.domus.tcc.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record DadosRegistrarLogConvidadoDTO(
    @NotNull(message = "Id do convidado é obrigatório.") Long idConvidado
) {
}
