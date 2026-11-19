package com.domus.tcc.backend.util;

public record ConvidadoChegouEvent(
    String nomeMorador,
    String emailMorador,
    String nomeConvidado,
    String moradia,
    String dataHoraChegada
) {
}
