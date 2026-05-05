CREATE TABLE categoria (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL UNIQUE,
    descricao      VARCHAR(255),
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMP    NOT NULL,
    atualizado_em  TIMESTAMP    NOT NULL,
    criado_por     VARCHAR(60),
    atualizado_por VARCHAR(60)
);

CREATE INDEX idx_categoria_nome  ON categoria (nome);
CREATE INDEX idx_categoria_ativo ON categoria (ativo);
