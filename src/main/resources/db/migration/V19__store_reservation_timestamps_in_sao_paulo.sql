UPDATE reserva_morador_espaco
SET criada_em = DATE_SUB(criada_em, INTERVAL 3 HOUR),
    cancelada_em = DATE_SUB(cancelada_em, INTERVAL 3 HOUR);
