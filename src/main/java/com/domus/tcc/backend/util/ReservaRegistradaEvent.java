package com.domus.tcc.backend.util;

public record ReservaRegistradaEvent(
        String destinatario,
        String template,
        String assunto,
        String nomeDestinatario,
        String nomeSindicoResponsavel,
        String nomeEspaco,
        String statusReserva,
        String dataReserva,
        Integer quantidadeConvidados,
        String justificativaReservaNegada,
        Double valorReserva
) {
}
