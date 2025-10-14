-- Criação da tabela de Roles
CREATE TABLE tb_roles (
    id_role BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Criação da tabela de Users
CREATE TABLE tb_users (
    id_user BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255),
    url_foto_perfil VARCHAR(255)
);

-- Criação da tabela de junção Users_Roles
CREATE TABLE tb_users_roles (
    id_user BIGINT NOT NULL,
    id_role BIGINT NOT NULL,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES tb_users (id_user),
    FOREIGN KEY (id_role) REFERENCES tb_roles (id_role)
);

-- Criação da tabela de Refresh Tokens
CREATE TABLE tb_refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (id_user) REFERENCES tb_users (id_user)
);

-- Insere as roles básicas
INSERT INTO tb_roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO tb_roles (name) VALUES ('ROLE_USER');