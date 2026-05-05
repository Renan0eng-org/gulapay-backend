CREATE TABLE entregador (
    id             BIGSERIAL    PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    telefone       VARCHAR(20)  NOT NULL,
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMP    NOT NULL,
    atualizado_em  TIMESTAMP    NOT NULL,
    criado_por     VARCHAR(60),
    atualizado_por VARCHAR(60)
);

CREATE INDEX idx_entregador_nome  ON entregador (nome);
CREATE INDEX idx_entregador_ativo ON entregador (ativo);
