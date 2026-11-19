ALTER TABLE convidado
  DROP FOREIGN KEY fk_convidado_tipo_visita1,
  DROP COLUMN tipo_visita_idtTipo_visita,
  DROP COLUMN observacao,
  ADD UNIQUE KEY uq_convidado_cpf_morador (cpf, morador_idMorador);

ALTER TABLE visitante
  ADD COLUMN idMoradia INT NOT NULL,
  ADD COLUMN tipo_visita ENUM('COMUM','SERVICO') NOT NULL,
  ADD CONSTRAINT fk_visitante_moradia_direta
    FOREIGN KEY (idMoradia) REFERENCES moradia (idMoradia);

UPDATE visitante v
  JOIN tipo_visita t ON t.idtTipo_visita = v.tipo_visita_idtipo_visita
  SET v.tipo_visita = CASE
    WHEN t.nome_visita = 'SERVICO' THEN 'SERVICO'
    ELSE 'COMUM'
  END;

ALTER TABLE visitante
  DROP FOREIGN KEY fk_visitante_tipo_visita1,
  DROP COLUMN tipo_visita_idtipo_visita;

DROP TABLE visitante_moradia;
DROP TABLE tipo_visita;

RENAME TABLE log_visita TO log_convidado;
RENAME TABLE visitante TO log_visita;
