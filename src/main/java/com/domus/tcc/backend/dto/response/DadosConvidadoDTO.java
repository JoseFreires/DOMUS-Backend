package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.Convidado;
import com.domus.tcc.backend.domain.enums.Recorrencia;

public record DadosConvidadoDTO(
    Long idConvidado,
    String nome,
    String cpf,
    String telefone,
    Recorrencia recorrencia,
    Boolean ativo
) {
    public DadosConvidadoDTO(Convidado convidado) {
        this(
            convidado.getIdConvidado(),
            convidado.getNome(),
            convidado.getCpf(),
            convidado.getTelefone(),
            convidado.getRecorrencia(),
            convidado.getAtivo()
        );
    }
}
