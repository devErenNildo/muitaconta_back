CREATE TABLE tb_cartao_credito (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    limite DECIMAL(19, 2) NOT NULL,
    dia_fechamento INT NOT NULL,
    dia_vencimento INT NOT NULL,
    id_conta BIGINT NOT NULL,
    CONSTRAINT fk_cartao_user FOREIGN KEY (id_conta) REFERENCES tb_contas(id)
);

CREATE TABLE tb_fatura (
    id BIGSERIAL PRIMARY KEY,
    mes INT NOT NULL,
    ano INT NOT NULL,
    valor_total DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) NOT NULL,
    id_cartao_credito BIGINT NOT NULL,
    CONSTRAINT fk_fatura_cartao FOREIGN KEY (id_cartao_credito) REFERENCES tb_cartao_credito(id)
);

CREATE TABLE tb_despesa_cartao (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    data DATE NOT NULL,
    numero_parcelas INT,
    id_fatura BIGINT NOT NULL,
    CONSTRAINT fk_despesa_fatura FOREIGN KEY (id_fatura) REFERENCES tb_fatura(id)
);