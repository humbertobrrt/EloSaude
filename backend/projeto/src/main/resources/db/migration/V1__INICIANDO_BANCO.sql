
CREATE TABLE usuario (
     id          BIGSERIAL    PRIMARY KEY,
     nome        VARCHAR(150) NOT NULL,
     email       VARCHAR(150) NOT NULL,
     senha_hash  VARCHAR(255) NOT NULL,
     perfil      VARCHAR(20)  NOT NULL,
     ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
     criado_em   TIMESTAMP    NOT NULL DEFAULT NOW(),
     CONSTRAINT uq_usuario_email  UNIQUE (email),
     CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('PACIENTE', 'PROFISSIONAL', 'ADMIN'))
);


CREATE TABLE unidade_saude (
   id              BIGSERIAL    PRIMARY KEY,
   nome            VARCHAR(150) NOT NULL,
   tipo            VARCHAR(20)  NOT NULL,
   cnes            VARCHAR(10),
   endereco        VARCHAR(200),
   bairro          VARCHAR(100),
   cidade          VARCHAR(100) NOT NULL,
   uf              CHAR(2)      NOT NULL,
   telefone        VARCHAR(20),
   especialidades  VARCHAR(500),
   ativa           BOOLEAN      NOT NULL DEFAULT TRUE,
   CONSTRAINT uq_unidade_cnes UNIQUE (cnes),
   CONSTRAINT ck_unidade_tipo CHECK (tipo IN ('UBS', 'ESPECIALIZADA', 'HOSPITAL'))
);


CREATE TABLE paciente (
      id                  BIGSERIAL    PRIMARY KEY,
      usuario_id          BIGINT       NOT NULL,
      cpf                 VARCHAR(11)  NOT NULL,
      cartao_sus          VARCHAR(15),
      data_nascimento     DATE         NOT NULL,
      sexo                VARCHAR(15),
      telefone            VARCHAR(20),
      endereco            VARCHAR(200),
      bairro              VARCHAR(100),
      cidade              VARCHAR(100),
      uf                  CHAR(2),
      ubs_referencia_id   BIGINT,
      condicoes_cronicas  VARCHAR(500),
      alergias            VARCHAR(500),
      CONSTRAINT uq_paciente_usuario    UNIQUE (usuario_id),
      CONSTRAINT uq_paciente_cpf        UNIQUE (cpf),
      CONSTRAINT uq_paciente_cartao_sus UNIQUE (cartao_sus),
      CONSTRAINT ck_paciente_cpf        CHECK (cpf ~ '^[0-9]{11}$'),
CONSTRAINT ck_paciente_sexo       CHECK (sexo IN ('FEMININO', 'MASCULINO', 'OUTRO')),
CONSTRAINT fk_paciente_usuario    FOREIGN KEY (usuario_id)        REFERENCES usuario (id),
CONSTRAINT fk_paciente_ubs_ref    FOREIGN KEY (ubs_referencia_id) REFERENCES unidade_saude (id)
);


CREATE TABLE profissional_saude (
    id                 BIGSERIAL    PRIMARY KEY,
    usuario_id         BIGINT       NOT NULL,
    unidade_id         BIGINT       NOT NULL,
    tipo_profissional  VARCHAR(20)  NOT NULL,
    registro_conselho  VARCHAR(30),
    especialidade      VARCHAR(100),
    CONSTRAINT uq_profissional_usuario UNIQUE (usuario_id),
    CONSTRAINT ck_profissional_tipo    CHECK (tipo_profissional IN
                                              ('MEDICO', 'ENFERMEIRO', 'TECNICO', 'ACS', 'RECEPCIONISTA', 'OUTRO')),
    CONSTRAINT fk_profissional_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_profissional_unidade FOREIGN KEY (unidade_id) REFERENCES unidade_saude (id)
);


CREATE TABLE solicitacao_atendimento (
     id                BIGSERIAL    PRIMARY KEY,
     paciente_id       BIGINT       NOT NULL,
     unidade_id        BIGINT       NOT NULL,
     tipo              VARCHAR(20)  NOT NULL,
     motivo            VARCHAR(500) NOT NULL,
     status            VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
     resposta_unidade  VARCHAR(500),
     data_prevista     TIMESTAMP,
     criado_em         TIMESTAMP    NOT NULL DEFAULT NOW(),
     atualizado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
     CONSTRAINT ck_solicitacao_tipo   CHECK (tipo IN ('FICHA', 'CONSULTA', 'RETORNO')),
     CONSTRAINT ck_solicitacao_status CHECK (status IN
                                             ('PENDENTE', 'EM_ANALISE', 'CONFIRMADA', 'CONCLUIDA', 'CANCELADA')),
     CONSTRAINT fk_solicitacao_paciente FOREIGN KEY (paciente_id) REFERENCES paciente (id),
     CONSTRAINT fk_solicitacao_unidade  FOREIGN KEY (unidade_id)  REFERENCES unidade_saude (id)
);


CREATE TABLE atendimento (
     id                 BIGSERIAL    PRIMARY KEY,
     paciente_id        BIGINT       NOT NULL,
     profissional_id    BIGINT       NOT NULL,
     unidade_id         BIGINT       NOT NULL,
     solicitacao_id     BIGINT,
     encaminhamento_id  BIGINT,
     data_hora          TIMESTAMP    NOT NULL DEFAULT NOW(),
     queixa_principal   VARCHAR(500),
     anamnese           TEXT,
     diagnostico        VARCHAR(500),
     cid10              VARCHAR(10),
     conduta            TEXT,
     prescricao         TEXT,
     observacoes        TEXT,
     CONSTRAINT fk_atendimento_paciente     FOREIGN KEY (paciente_id)     REFERENCES paciente (id),
     CONSTRAINT fk_atendimento_profissional FOREIGN KEY (profissional_id) REFERENCES profissional_saude (id),
     CONSTRAINT fk_atendimento_unidade      FOREIGN KEY (unidade_id)      REFERENCES unidade_saude (id),
     CONSTRAINT fk_atendimento_solicitacao  FOREIGN KEY (solicitacao_id)  REFERENCES solicitacao_atendimento (id)
);


CREATE TABLE encaminhamento (
    id                           BIGSERIAL    PRIMARY KEY,
    paciente_id                  BIGINT       NOT NULL,
    atendimento_origem_id        BIGINT,
    unidade_origem_id            BIGINT       NOT NULL,
    unidade_destino_id           BIGINT       NOT NULL,
    profissional_solicitante_id  BIGINT       NOT NULL,
    especialidade_destino        VARCHAR(100),
    prioridade                   VARCHAR(10)  NOT NULL DEFAULT 'MEDIA',
    motivo                       VARCHAR(500) NOT NULL,
    resumo_clinico               TEXT,
    compartilhar_historico       BOOLEAN      NOT NULL DEFAULT FALSE,
    status                       VARCHAR(20)  NOT NULL DEFAULT 'CRIADO',
    criado_em                    TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em                TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_encaminhamento_prioridade CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA', 'URGENTE')),
    CONSTRAINT ck_encaminhamento_status     CHECK (status IN
                                                   ('CRIADO', 'ENVIADO', 'RECEBIDO', 'EM_ATENDIMENTO', 'CONCLUIDO', 'CANCELADO')),
    CONSTRAINT ck_encaminhamento_unidades   CHECK (unidade_destino_id <> unidade_origem_id),
    CONSTRAINT fk_encaminhamento_paciente     FOREIGN KEY (paciente_id)                REFERENCES paciente (id),
    CONSTRAINT fk_encaminhamento_atend_origem FOREIGN KEY (atendimento_origem_id)      REFERENCES atendimento (id),
    CONSTRAINT fk_encaminhamento_un_origem    FOREIGN KEY (unidade_origem_id)          REFERENCES unidade_saude (id),
    CONSTRAINT fk_encaminhamento_un_destino   FOREIGN KEY (unidade_destino_id)         REFERENCES unidade_saude (id),
    CONSTRAINT fk_encaminhamento_profissional FOREIGN KEY (profissional_solicitante_id) REFERENCES profissional_saude (id)
);


ALTER TABLE atendimento
ADD CONSTRAINT fk_atendimento_encaminhamento
FOREIGN KEY (encaminhamento_id) REFERENCES encaminhamento (id);


CREATE TABLE contra_referencia (
   id                          BIGSERIAL  PRIMARY KEY,
   encaminhamento_id           BIGINT     NOT NULL,
   atendimento_destino_id      BIGINT,
   profissional_id             BIGINT     NOT NULL,
   resumo_atendimento          TEXT       NOT NULL,
   plano_cuidado               TEXT,
   orientacoes_unidade_origem  TEXT,
   retorno_recomendado_em      DATE,
   criado_em                   TIMESTAMP  NOT NULL DEFAULT NOW(),
   CONSTRAINT uq_contra_ref_encaminhamento UNIQUE (encaminhamento_id),
   CONSTRAINT fk_contra_ref_encaminhamento FOREIGN KEY (encaminhamento_id)      REFERENCES encaminhamento (id),
   CONSTRAINT fk_contra_ref_atend_destino  FOREIGN KEY (atendimento_destino_id) REFERENCES atendimento (id),
   CONSTRAINT fk_contra_ref_profissional   FOREIGN KEY (profissional_id)        REFERENCES profissional_saude (id)
);


CREATE INDEX idx_solicitacao_paciente   ON solicitacao_atendimento (paciente_id);
CREATE INDEX idx_solicitacao_unidade    ON solicitacao_atendimento (unidade_id, status);
CREATE INDEX idx_atendimento_paciente   ON atendimento (paciente_id, data_hora DESC);
CREATE INDEX idx_encaminhamento_paciente ON encaminhamento (paciente_id);
CREATE INDEX idx_encaminhamento_destino ON encaminhamento (unidade_destino_id, status);
CREATE INDEX idx_encaminhamento_origem  ON encaminhamento (unidade_origem_id, status);
CREATE INDEX idx_profissional_unidade   ON profissional_saude (unidade_id);