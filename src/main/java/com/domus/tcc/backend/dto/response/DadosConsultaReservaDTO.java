package com.domus.tcc.backend.dto.response;

import com.domus.tcc.backend.domain.ReservaCondominial;
import com.domus.tcc.backend.domain.enums.StatusReserva;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record DadosConsultaReservaDTO(
        Long idReserva,
        LocalDate dataReserva,
        Integer quantidadeConvidados,
        StatusReserva statusReserva,
        String justificativaReservaNegada,
        Long idEspaco,
        String nomeEspaco,
        Long idMorador,
        String nomeMorador,
        String criadaEm,
        String canceladaEm
) {
    private static final ZoneId FUSO_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    public DadosConsultaReservaDTO(ReservaCondominial reserva) {
        this(
                reserva.getIdReserva(),
                reserva.getDataReserva(),
                reserva.getQuantidadeConvidados(),
                reserva.getStatusReserva(),
                reserva.getJustificativaReservaNegada(),
                reserva.getEspaco().getIdEspacoCondominial(),
                reserva.getEspaco().getNomeEspaco(),
                reserva.getMorador().getId(),
                reserva.getMorador().getPessoa().getNomeCompleto(),
                formatarEmSaoPaulo(reserva.getCriadaEm()),
                formatarEmSaoPaulo(reserva.getCanceladaEm())
        );
    }

    private static String formatarEmSaoPaulo(Instant instante) {
        return instante == null
                ? null
                : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instante.atZone(FUSO_SAO_PAULO));
    }
}
