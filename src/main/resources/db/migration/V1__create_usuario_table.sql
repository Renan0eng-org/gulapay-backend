CREATE TABLE usuario (
    id            BIGSERIAL PRIMARY KEY,
    login         VARCHAR(60)  NOT NULL UNIQUE,
    nome          VARCHAR(120) NOT NULL,
    senha         VARCHAR(120) NOT NULL,
    perfil        VARCHAR(20)  NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL,
    atualizado_em TIMESTAMP    NOT NULL,
    criado_por    VARCHAR(60),
    atualizado_por VARCHAR(60),
    CONSTRAINT chk_usuario_perfil CHECK (perfil IN ('ADMINISTRADOR', 'CAIXA', 'GARCOM'))
);

CREATE INDEX idx_usuario_login ON usuario (login);
CREATE INDEX idx_usuario_perfil ON usuario (perfil);
