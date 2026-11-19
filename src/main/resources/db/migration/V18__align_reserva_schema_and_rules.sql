ALTER TABLE reserva_morador_espaco
    DROP FOREIGN KEY fk_reserva_morador_espaco_espaco_condominial1,
    DROP FOREIGN KEY fk_reserva_morador_espaco_morador1,
    DROP INDEX fk_reserva_morador_espaco_espaco_condominial1_idx,
    DROP INDEX fk_reserva_morador_espaco_morador1_idx;

ALTER TABLE reserva_morador_espaco
    RENAME COLUMN idReserva TO id_reserva,
    RENAME COLUMN espaco_condominial_idEspaco_condominial TO espaco_id,
    RENAME COLUMN morador_idMorador TO morador_id;

ALTER TABLE reserva_morador_espaco
    MODIFY COLUMN quantidade_convidados INT NOT NULL,
    MODIFY COLUMN status_reserva
        ENUM('CONFIRMADA', 'RESERVADA', 'CANCELADA', 'CONCLUIDA', 'NEGADA')
        NOT NULL,
    MODIFY COLUMN justificativa_reserva_negada VARCHAR(500) NULL,
    ADD COLUMN criada_em DATETIME(6) NULL,
    ADD COLUMN cancelada_em DATETIME(6) NULL,
    ADD COLUMN motivo_cancelamento VARCHAR(40) NULL;

UPDATE reserva_morador_espaco
SET criada_em = UTC_TIMESTAMP(6)
WHERE criada_em IS NULL;

ALTER TABLE reserva_morador_espaco
    MODIFY COLUMN criada_em DATETIME(6) NOT NULL;

CREATE INDEX idx_reserva_espaco_data_status
    ON reserva_morador_espaco (espaco_id, data_reserva, status_reserva);

CREATE INDEX idx_reserva_morador_data_status
    ON reserva_morador_espaco (morador_id, data_reserva, status_reserva);

CREATE INDEX idx_reserva_status_criada_em
    ON reserva_morador_espaco (status_reserva, criada_em);

ALTER TABLE reserva_morador_espaco
    ADD CONSTRAINT fk_reserva_espaco
        FOREIGN KEY (espaco_id)
        REFERENCES espaco_condominial (idEspaco_condominial)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    ADD CONSTRAINT fk_reserva_morador
        FOREIGN KEY (morador_id)
        REFERENCES morador (idMorador)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;
