CREATE TABLE cliente (
    id                   BIGSERIAL    PRIMARY KEY,
    nome                 VARCHAR(120) NOT NULL,
    telefone             VARCHAR(20)  NOT NULL UNIQUE,
    email                VARCHAR(120),
    endereco_logradouro  VARCHAR(150),
    endereco_numero      VARCHAR(20),
    endereco_complemento VARCHAR(80),
    endereco_bairro      VARCHAR(80),
    endereco_cidade      VARCHAR(80),
    endereco_uf          VARCHAR(2),
    endereco_cep         VARCHAR(10),
    ativo                BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em            TIMESTAMP    NOT NULL,
    atualizado_em        TIMESTAMP    NOT NULL,
    criado_por           VARCHAR(60),
    atualizado_por       VARCHAR(60)
);

CREATE INDEX idx_cliente_telefone ON cliente (telefone);
CREATE INDEX idx_cliente_nome     ON cliente (nome);
CREATE INDEX idx_cliente_ativo    ON cliente (ativo);
