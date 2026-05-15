CREATE TABLE unidade_medida (
    id              BIGSERIAL     PRIMARY KEY,
    nome            VARCHAR(60)   NOT NULL UNIQUE,
    simbolo         VARCHAR(10)   NOT NULL UNIQUE,
    tipo_medida     VARCHAR(20)   NOT NULL,
    fator_para_base DECIMAL(18,6) NOT NULL,
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP     NOT NULL,
    atualizado_em   TIMESTAMP     NOT NULL,
    criado_por      VARCHAR(60),
    atualizado_por  VARCHAR(60),
    CONSTRAINT chk_unidade_tipo  CHECK (tipo_medida IN ('MASSA', 'VOLUME', 'UNIDADE')),
    CONSTRAINT chk_unidade_fator CHECK (fator_para_base > 0)
);

CREATE INDEX idx_unidade_tipo  ON unidade_medida (tipo_medida);
CREATE INDEX idx_unidade_ativo ON unidade_medida (ativo);

-- Seed das unidades-base e auxiliares (decisão 4.5.1 do CLAUDE.md)
INSERT INTO unidade_medida (nome, simbolo, tipo_medida, fator_para_base, ativo, criado_em, atualizado_em, criado_por, atualizado_por) VALUES
('Grama',      'g',  'MASSA',   1,    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('Quilograma', 'kg', 'MASSA',   1000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('Mililitro',  'mL', 'VOLUME',  1,    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('Litro',      'L',  'VOLUME',  1000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('Unidade',    'un', 'UNIDADE', 1,    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
('Dúzia',      'dz', 'UNIDADE', 12,   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');
