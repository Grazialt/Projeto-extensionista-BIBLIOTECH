-- Garante que a coluna turma existe na tabela usuario.
-- ALTER TABLE ... ADD COLUMN IF NOT EXISTS é suportado pelo PostgreSQL 9.6+
-- e é seguro rodar mais de uma vez.
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS turma VARCHAR(255);

ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS data_prevista_devolucao DATE;
ALTER TABLE emprestimo ADD COLUMN IF NOT EXISTS data_real_devolucao DATE;

UPDATE emprestimo
SET data_real_devolucao = data_devolucao,
	data_prevista_devolucao = NULL
WHERE status = 'devolvido' AND data_real_devolucao IS NULL;

UPDATE emprestimo
SET data_prevista_devolucao = data_devolucao
WHERE status = 'ativo' AND data_prevista_devolucao IS NULL;
