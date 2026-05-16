-- Etapa 2.1: liga Produto ao estoque via Insumo (apenas para tipoProduto = UNITARIO).
-- Estratégia (b) do CLAUDE.md 4.5.2: cria automaticamente um Insumo-espelho para
-- cada Produto UNITARIO existente, com unidadePadrao = 'un' e estoque mínimo zero.

ALTER TABLE produto ADD COLUMN insumo_id BIGINT;

-- Coluna temporária para correlacionar produto -> insumo recém-criado.
ALTER TABLE insumo ADD COLUMN _migration_produto_id BIGINT;

INSERT INTO insumo (
    nome, unidade_padrao_id, estoque_minimo, ativo,
    criado_em, atualizado_em, criado_por, atualizado_por,
    _migration_produto_id
)
SELECT
    p.nome,
    (SELECT id FROM unidade_medida WHERE simbolo = 'un' LIMIT 1),
    0,
    TRUE,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    'system', 'system',
    p.id
FROM produto p
WHERE p.tipo_produto = 'UNITARIO';

UPDATE produto
SET insumo_id = (
    SELECT i.id FROM insumo i WHERE i._migration_produto_id = produto.id
)
WHERE tipo_produto = 'UNITARIO';

ALTER TABLE insumo DROP COLUMN _migration_produto_id;

ALTER TABLE produto ADD CONSTRAINT fk_produto_insumo
    FOREIGN KEY (insumo_id) REFERENCES insumo (id);

ALTER TABLE produto ADD CONSTRAINT chk_produto_unitario_tem_insumo
    CHECK (
        (tipo_produto = 'UNITARIO' AND insumo_id IS NOT NULL) OR
        (tipo_produto IN ('COMPOSTO', 'COMBO') AND insumo_id IS NULL)
    );

CREATE INDEX idx_produto_insumo ON produto (insumo_id);
