package com.domus.tcc.backend.dto.request;

import com.domus.tcc.backend.domain.enums.TipoVisita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosRegistrarLogVisitaDTO(
    @NotBlank(message = "Nome é obrigatório.") String nome,
    @NotNull(message = "Tipo de visita é obrigatório.") TipoVisita tipoVisita,
    @NotNull(message = "Id da moradia é obrigatório.") Long idMoradia
) {
}
