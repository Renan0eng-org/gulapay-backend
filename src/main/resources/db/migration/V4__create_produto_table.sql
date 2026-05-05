CREATE TABLE produto (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(120)   NOT NULL,
    descricao      VARCHAR(500),
    preco          DECIMAL(10,2)  NOT NULL,
    tipo_produto   VARCHAR(20)    NOT NULL,
    setor_producao VARCHAR(20)    NOT NULL,
    categoria_id   BIGINT         NOT NULL,
    ativo          BOOLEAN        NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMP      NOT NULL,
    atualizado_em  TIMESTAMP      NOT NULL,
    criado_por     VARCHAR(60),
    atualizado_por VARCHAR(60),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT chk_produto_tipo     CHECK (tipo_produto   IN ('UNITARIO', 'COMPOSTO', 'COMBO')),
    CONSTRAINT chk_produto_setor    CHECK (setor_producao IN ('COZINHA', 'BAR', 'BALCAO')),
    CONSTRAINT chk_produto_preco    CHECK (preco >= 0)
);

CREATE INDEX idx_produto_categoria ON produto (categoria_id);
CREATE INDEX idx_produto_nome      ON produto (nome);
CREATE INDEX idx_produto_ativo     ON produto (ativo);
