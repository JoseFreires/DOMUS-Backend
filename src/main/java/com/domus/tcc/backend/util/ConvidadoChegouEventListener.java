package com.domus.tcc.backend.util;

import com.domus.tcc.backend.services.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class ConvidadoChegouEventListener {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    public ConvidadoChegouEventListener(EmailService emailService, SpringTemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConvidadoChegou(ConvidadoChegouEvent evento) {
        Context context = new Context();
        context.setVariable("nomeMorador", evento.nomeMorador());
        context.setVariable("nomeConvidado", evento.nomeConvidado());
        context.setVariable("moradia", evento.moradia());
        context.setVariable("dataHoraChegada", evento.dataHoraChegada());

        String htmlBody = templateEngine.process("email-convidado-chegou", context);
        emailService.enviarEmailHtml(
            evento.emailMorador(),
            "Seu convidado chegou!",
            htmlBody
        );
    }
}
