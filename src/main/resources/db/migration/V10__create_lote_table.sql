CREATE TABLE lote (
    id                   BIGSERIAL     PRIMARY KEY,
    insumo_id            BIGINT        NOT NULL,
    codigo               VARCHAR(60),
    validade             DATE          NOT NULL,
    quantidade_inicial   DECIMAL(18,3) NOT NULL,
    quantidade_restante  DECIMAL(18,3) NOT NULL,
    custo_unitario       DECIMAL(18,4) NOT NULL DEFAULT 0,
    ativo                BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em            TIMESTAMP     NOT NULL,
    atualizado_em        TIMESTAMP     NOT NULL,
    criado_por           VARCHAR(60),
    atualizado_por       VARCHAR(60),
    CONSTRAINT fk_lote_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id),
    CONSTRAINT chk_lote_qtd_inicial   CHECK (quantidade_inicial >= 0),
    CONSTRAINT chk_lote_qtd_restante  CHECK (quantidade_restante >= 0)
);

CREATE INDEX idx_lote_insumo            ON lote (insumo_id);
CREATE INDEX idx_lote_validade          ON lote (validade);
CREATE INDEX idx_lote_ativo             ON lote (ativo);
CREATE INDEX idx_lote_insumo_validade   ON lote (insumo_id, validade);
