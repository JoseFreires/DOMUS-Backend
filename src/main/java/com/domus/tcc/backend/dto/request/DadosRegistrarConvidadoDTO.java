package com.domus.tcc.backend.dto.request;

import com.domus.tcc.backend.domain.enums.Recorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosRegistrarConvidadoDTO(
    @NotBlank(message = "Nome é obrigatório.") String nome,
    @NotBlank(message = "CPF é obrigatório.") String cpf,
    @NotBlank(message = "Telefone é obrigatório.") String telefone,
    @NotNull(message = "Recorrência é obrigatória.") Recorrencia recorrencia
) {
}
