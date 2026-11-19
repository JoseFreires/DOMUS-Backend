package com.domus.tcc.backend.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DadosAtualizacaoReservaDTO(

        @NotNull(message = "A quantidade de convidados é obrigatória")
        @PositiveOrZero(message = "A quantidade de convidados não pode ser negativa")
        Integer quantidadeConvidados

) {
}
