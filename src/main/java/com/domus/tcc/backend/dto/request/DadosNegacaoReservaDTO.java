package com.domus.tcc.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosNegacaoReservaDTO(

        @NotBlank(message = "A justificativa é obrigatória para negar a reserva")
        @Size(max = 500, message = "A justificativa deve ter no máximo 500 caracteres")
        String justificativa

) {
}
