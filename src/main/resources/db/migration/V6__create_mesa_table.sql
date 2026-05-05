CREATE TABLE mesa (
    id             BIGSERIAL    PRIMARY KEY,
    numero         VARCHAR(20)  NOT NULL UNIQUE,
    descricao      VARCHAR(120),
    capacidade     INTEGER      NOT NULL DEFAULT 4,
    status         VARCHAR(30)  NOT NULL DEFAULT 'LIVRE',
    ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMP    NOT NULL,
    atualizado_em  TIMESTAMP    NOT NULL,
    criado_por     VARCHAR(60),
    atualizado_por VARCHAR(60),
    CONSTRAINT chk_mesa_status     CHECK (status IN ('LIVRE','OCUPADA','AGUARDANDO_PAGAMENTO','FECHADA')),
    CONSTRAINT chk_mesa_capacidade CHECK (capacidade > 0)
);

CREATE INDEX idx_mesa_numero ON mesa (numero);
CREATE INDEX idx_mesa_status ON mesa (status);
CREATE INDEX idx_mesa_ativo  ON mesa (ativo);
