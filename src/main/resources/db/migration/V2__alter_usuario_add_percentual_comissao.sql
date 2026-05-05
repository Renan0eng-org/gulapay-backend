ALTER TABLE usuario ADD COLUMN percentual_comissao DECIMAL(5,2);

ALTER TABLE usuario ADD CONSTRAINT chk_usuario_percentual_comissao
    CHECK (percentual_comissao IS NULL OR (percentual_comissao >= 0 AND percentual_comissao <= 100));
