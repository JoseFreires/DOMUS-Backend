-- 1. Migra os vínculos antigos para a tabela associativa
INSERT IGNORE INTO morador_moradia (
    morador_idMorador,
    moradia_idMoradia
)
SELECT
    idMorador,
    moradia_idMoradia
FROM morador
WHERE moradia_idMoradia IS NOT NULL;

-- 2. Remove a chave estrangeira antiga
ALTER TABLE morador
    DROP FOREIGN KEY fk_morador_moradia1;

-- 3. Remove o índice criado para a chave estrangeira
ALTER TABLE morador
    DROP INDEX fk_morador_moradia1_idx;

-- 4. Remove a coluna antiga
ALTER TABLE morador
    DROP COLUMN moradia_idMoradia;