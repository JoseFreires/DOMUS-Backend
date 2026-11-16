package com.domus.tcc.backend.util;

import com.domus.tcc.backend.services.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class ReservaEventListener {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;


    public ReservaEventListener(
            EmailService emailService,
            SpringTemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmailReserva(ReservaRegistradaEvent evento) {
        Context ctx = new Context();
        ctx.setVariable("nomeDestinatario", evento.nomeDestinatario());
        ctx.setVariable("nomeEspaco", evento.nomeEspaco());
        ctx.setVariable("nomeSindicoResponsavel", evento.nomeSindicoResponsavel());
        ctx.setVariable("statusReserva",  evento.statusReserva());
        ctx.setVariable("dataReserva",  evento.dataReserva());
        ctx.setVariable("quantidadeConvidados",  evento.quantidadeConvidados());
        ctx.setVariable("justificativaReservaNegada", evento.justificativaReservaNegada());
        ctx.setVariable("valorReserva", evento.valorReserva());

        String htmlBody = templateEngine.process(evento.template(), ctx);

        emailService.enviarEmailHtml(
                evento.destinatario(),
                evento.assunto(),
                htmlBody
        );
    }

}
