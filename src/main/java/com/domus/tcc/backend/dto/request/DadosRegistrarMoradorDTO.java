package com.domus.tcc.backend.dto.request;

import com.domus.tcc.backend.dto.DadosLoginDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record DadosRegistrarMoradorDTO(

        @NotNull @Valid
        DadosRegistrarPessoaDTO pessoa,

        @NotNull @Valid
        DadosLoginDTO usuario,

        @NotEmpty(message = "Ao menos uma moradia/apartamento deve ser informado")
        List<@NotNull(message = "O ID da moradia/apartamento é obrigatório") Long> idMoradias,

        @NotNull(message = "A data de chegada ao condomínio é obrigatória")
        LocalDate dataChegada
) {
}