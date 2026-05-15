CREATE TABLE insumo (
    id                 BIGSERIAL     PRIMARY KEY,
    nome               VARCHAR(120)  NOT NULL,
    unidade_padrao_id  BIGINT        NOT NULL,
    estoque_minimo     DECIMAL(18,3) NOT NULL DEFAULT 0,
    ativo              BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em          TIMESTAMP     NOT NULL,
    atualizado_em      TIMESTAMP     NOT NULL,
    criado_por         VARCHAR(60),
    atualizado_por     VARCHAR(60),
    CONSTRAINT fk_insumo_unidade FOREIGN KEY (unidade_padrao_id) REFERENCES unidade_medida (id),
    CONSTRAINT chk_insumo_estoque_min CHECK (estoque_minimo >= 0)
);

CREATE INDEX idx_insumo_nome    ON insumo (nome);
CREATE INDEX idx_insumo_unidade ON insumo (unidade_padrao_id);
CREATE INDEX idx_insumo_ativo   ON insumo (ativo);
