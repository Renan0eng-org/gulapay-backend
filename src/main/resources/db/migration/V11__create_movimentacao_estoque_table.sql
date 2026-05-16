CREATE TABLE movimentacao_estoque (
    id                          BIGSERIAL     PRIMARY KEY,
    tipo                        VARCHAR(30)   NOT NULL,
    insumo_id                   BIGINT        NOT NULL,
    lote_id                     BIGINT,
    unidade_id                  BIGINT        NOT NULL,
    quantidade                  DECIMAL(18,3) NOT NULL,
    quantidade_unidade_padrao   DECIMAL(18,3) NOT NULL,
    custo_unitario              DECIMAL(18,4),
    justificativa               VARCHAR(500),
    data_hora                   TIMESTAMP     NOT NULL,
    responsavel                 VARCHAR(60),
    criado_em                   TIMESTAMP     NOT NULL,
    atualizado_em               TIMESTAMP     NOT NULL,
    criado_por                  VARCHAR(60),
    atualizado_por              VARCHAR(60),
    CONSTRAINT fk_mov_insumo  FOREIGN KEY (insumo_id)  REFERENCES insumo (id),
    CONSTRAINT fk_mov_lote    FOREIGN KEY (lote_id)    REFERENCES lote (id),
    CONSTRAINT fk_mov_unidade FOREIGN KEY (unidade_id) REFERENCES unidade_medida (id),
    CONSTRAINT chk_mov_tipo CHECK (tipo IN (
        'ENTRADA_COMPRA',
        'ENTRADA_TROCA',
        'SAIDA_VENDA',
        'SAIDA_PERDA_VALIDADE',
        'SAIDA_PERDA_QUEBRA',
        'AJUSTE_INVENTARIO'
    )),
    CONSTRAINT chk_mov_quantidade CHECK (quantidade > 0)
);

CREATE INDEX idx_mov_insumo    ON movimentacao_estoque (insumo_id);
CREATE INDEX idx_mov_lote      ON movimentacao_estoque (lote_id);
CREATE INDEX idx_mov_tipo      ON movimentacao_estoque (tipo);
CREATE INDEX idx_mov_data_hora ON movimentacao_estoque (data_hora);
