CREATE TABLE tb_user_provider (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    id_user BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES tb_users(id_user) ON DELETE CASCADE
);

CREATE INDEX idx_user_provider_id_user ON tb_user_provider(id_user);