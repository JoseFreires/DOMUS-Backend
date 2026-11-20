package com.domus.tcc.backend.dto.response;


import java.time.LocalDate;
import java.util.List;

import com.domus.tcc.backend.security.Usuario;

public record DadosConsultaMoradorDTO(
        Long idMorador,
        Long idPessoa,
        List<DadosConsultaMoradorMoradiaDTO> moradias,
        String nomeCompleto,
        String email,
        String cpf,
        LocalDate dataChegada,
        LocalDate dataNascimento,
        String telefone,
        Boolean ativo

) {
    public DadosConsultaMoradorDTO(Usuario usuario) {
        this(
                usuario.getPessoa().getMorador().getId(),
                usuario.getPessoa().getId(),
                usuario.getPessoa().getMorador().getMoradias().stream()
                        .map(DadosConsultaMoradorMoradiaDTO::new)
                        .toList(),
                usuario.getPessoa().getNomeCompleto(),
                usuario.getPessoa().getEmail(),
                usuario.getPessoa().getCpf(),
                usuario.getPessoa().getMorador().getDataChegada(),
                usuario.getPessoa().getDataNascimento(),
                usuario.getPessoa().getTelefone(),
                usuario.getPessoa().getAtivo()

        );

    }

}