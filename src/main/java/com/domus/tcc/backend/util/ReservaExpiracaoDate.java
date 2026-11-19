package com.domus.tcc.backend.util;

import com.domus.tcc.backend.services.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaExpiracaoDate {

    private static final Logger log = LoggerFactory.getLogger(ReservaExpiracaoDate.class);
    private final ReservaService reservaService;

    public ReservaExpiracaoDate(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Scheduled(cron = "0 0 5 * * *", zone = "America/Sao_Paulo")
    public void expirarPendentes() {
        int quantidade = reservaService.expirarSolicitacoesSemDecisao();
        if (quantidade > 0) {
            log.info("{} reserva(s) expiradas automaticamente", quantidade);
        }
    }

    @Scheduled(cron = "0 1 0 * * *", zone = "America/Sao_Paulo")
    public void concluirReservasVencidas() {
        int quantidade = reservaService.concluirReservasVencidas();
        if (quantidade > 0) {
            log.info("{} reserva(s) concluídas automaticamente", quantidade);
        }
    }
}
