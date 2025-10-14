CREATE TABLE tb_contas (
    id BIGSERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL,
    CONSTRAINT uk_contas_id_user UNIQUE (id_user),
    CONSTRAINT fk_contas_id_user FOREIGN KEY (id_user) REFERENCES tb_users(id_user)
);

CREATE TABLE tb_balanco_mensal (
    id BIGSERIAL PRIMARY KEY,
    ano INT NOT NULL,
    mes INT NOT NULL,
    total DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    id_conta BIGINT NOT NULL,
    CONSTRAINT fk_balanco_id_conta FOREIGN KEY (id_conta) REFERENCES tb_contas(id)
);