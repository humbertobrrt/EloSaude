CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE paciente      ALTER COLUMN uf TYPE VARCHAR(2);
ALTER TABLE unidade_saude ALTER COLUMN uf TYPE VARCHAR(2);

INSERT INTO usuario (nome, email, senha_hash, perfil, ativo, criado_em)
SELECT v.nome, v.email, crypt('Demo@2026', gen_salt('bf', 10)), v.perfil, TRUE, NOW()
FROM (
    VALUES
        ('Administrador',    'admin@elosaude.com',                'ADMIN'),
        ('Fabiana',          'paciente@elosaude.com',             'PACIENTE'),
        ('Marco Antonio',    'paciente.sj@elosaude.com',          'PACIENTE'),
        ('Sydney',           'paciente.charqueadas@elosaude.com', 'PACIENTE'),
        ('Dra. Flavia',      'ubs@elosaude.com',                  'PROFISSIONAL'),
        ('Dra. Clelia',      'hospital@elosaude.com',             'PROFISSIONAL'),
        ('Dr. Luiz Claudio', 'cardio@elosaude.com',               'PROFISSIONAL'),
        ('Dra. Grasi',       'profissional.sj@elosaude.com',      'PROFISSIONAL')
) AS v (nome, email, perfil)
WHERE NOT EXISTS (
    SELECT 1 FROM usuario u WHERE u.email = v.email
);

INSERT INTO unidade_saude
    (nome, tipo, cnes, endereco, bairro, cidade, uf, telefone, especialidades, ativa)
SELECT v.nome, v.tipo, v.cnes, v.endereco, v.bairro, v.cidade, v.uf,
       v.telefone, v.especialidades, TRUE
FROM (
    VALUES
        ('Unidade de Saúde da Família Central',
         'UBS', '2225042', 'Av. Farroupilha, 400', 'Centro',
         'Charqueadas', 'RS', '(51) 3958-8462',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Cruz de Malta',
         'UBS', '2225018', 'Rua Rio Grande do Norte, 620', 'Cruz de Malta',
         'Charqueadas', 'RS', '(51) 3958-8473',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Osmar Wienke',
         'UBS', '6915183', 'Rua José Manoel Gonzales de Souza, 761', 'Parque Manoel João',
         'Charqueadas', 'RS', '(51) 3958-8442',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Santo Antônio',
         'UBS', '2225034', 'Rua Arantes do Nascimento, 77', 'Santo Antônio',
         'Charqueadas', 'RS', '(51) 3658-8465',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família São Francisco',
         'UBS', '7320795', 'Rua Piauí', 'Vila Rosa',
         'Charqueadas', 'RS', '(51) 3958-8442',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família São Miguel',
         'UBS', '2224992', 'Avenida Colômbia, 330', 'São Miguel',
         'Charqueadas', 'RS', '(51) 3958-8466',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Beira Rio',
         'UBS', '2225026', 'Rua José Rui de Ruíz, 957', 'Vila Rosa',
         'Charqueadas', 'RS', '(51) 3958-8450',
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Piratini',
         'UBS', '2793202', 'Núcleo C-78', 'Vila Aços Finos Piratini',
         'Charqueadas', 'RS', NULL::text,
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Sul América',
         'UBS', '2225050', NULL::text, 'Sul América',
         'Charqueadas', 'RS', NULL::text,
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Unidade de Saúde da Família Vila Otília',
         'UBS', '5653649', 'Rua Acre, 290', 'Vila Otília',
         'Charqueadas', 'RS', NULL::text,
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Ambulatório Municipal de Charqueadas',
         'ESPECIALIZADA', '2225077', NULL::text, 'Centro',
         'Charqueadas', 'RS', NULL::text,
         'Atendimento ambulatorial e especialidades médicas'),

        ('Hospital de Charqueadas - Associação Hospitalar Vila Nova',
         'HOSPITAL', '9528792', 'RS-401, nº 1754', 'Centro',
         'Charqueadas', 'RS', '(51) 3658-3962',
         'Emergência, Clínica médica e atendimento hospitalar'),

        ('UBS Centro de São Jerônimo (DEMO)',
         'UBS', NULL::text, NULL::text, 'Centro',
         'São Jerônimo', 'RS', NULL::text,
         'Clínica geral, Enfermagem, Saúde da família'),

        ('UBS Interior de São Jerônimo (DEMO)',
         'UBS', NULL::text, NULL::text, NULL::text,
         'São Jerônimo', 'RS', NULL::text,
         'Clínica geral, Enfermagem, Saúde da família'),

        ('Hospital de São Jerônimo (DEMO)',
         'HOSPITAL', NULL::text, NULL::text, NULL::text,
         'São Jerônimo', 'RS', NULL::text,
         'Atendimento hospitalar')
) AS v (nome, tipo, cnes, endereco, bairro, cidade, uf, telefone, especialidades)
WHERE NOT EXISTS (
    SELECT 1 FROM unidade_saude un
    WHERE un.nome = v.nome AND un.cidade = v.cidade
);

INSERT INTO paciente
    (usuario_id, cpf, cartao_sus, data_nascimento, sexo, telefone,
     endereco, bairro, cidade, uf, ubs_referencia_id,
     condicoes_cronicas, alergias)
SELECT
    u.id, v.cpf, v.cartao_sus, v.data_nascimento, v.sexo, v.telefone,
    v.endereco, v.bairro, v.cidade, 'RS', un.id,
    v.condicoes_cronicas, v.alergias
FROM (
    VALUES
        ('paciente@elosaude.com',
         '90000000001', '000000000000001', DATE '2006-01-31', 'MASCULINO', '(51) 90000-0001',
         'Rua de demonstração, 20', 'Centro',
         'Charqueadas', 'Unidade de Saúde da Família Central',
         'Hipertensão', 'Nenhuma conhecida'),

        ('paciente.sj@elosaude.com',
         '90000000002', '000000000000002', DATE '1998-05-12', 'FEMININO', '(51) 90000-0002',
         'Rua de demonstração, 45', 'Centro',
         'São Jerônimo', 'UBS Centro de São Jerônimo (DEMO)',
         'Nenhuma informada', 'Nenhuma conhecida'),

        ('paciente.charqueadas@elosaude.com',
         '90000000003', '000000000000003', DATE '1985-09-22', 'FEMININO', '(51) 90000-0003',
         'Rua de demonstração, 80', 'Cruz de Malta',
         'Charqueadas', 'Unidade de Saúde da Família Cruz de Malta',
         'Diabetes tipo 2', 'Nenhuma conhecida')
) AS v (email, cpf, cartao_sus, data_nascimento, sexo, telefone,
        endereco, bairro, cidade, unidade, condicoes_cronicas, alergias)
JOIN usuario u        ON u.email = v.email
JOIN unidade_saude un ON un.nome = v.unidade AND un.cidade = v.cidade
WHERE NOT EXISTS (
    SELECT 1 FROM paciente p WHERE p.usuario_id = u.id
);

INSERT INTO profissional_saude
    (usuario_id, unidade_id, tipo_profissional, registro_conselho, especialidade)
SELECT u.id, un.id, v.tipo, v.registro, v.especialidade
FROM (
    VALUES
        ('ubs@elosaude.com',
         'Unidade de Saúde da Família Central',
         'Charqueadas', 'MEDICO', 'CRM-RS DEMO-00001', 'Clínica geral'),

        ('hospital@elosaude.com',
         'Hospital de Charqueadas - Associação Hospitalar Vila Nova',
         'Charqueadas', 'MEDICO', 'CRM-RS DEMO-00002', 'Emergência'),

        ('cardio@elosaude.com',
         'Ambulatório Municipal de Charqueadas',
         'Charqueadas', 'MEDICO', 'CRM-RS DEMO-00003', 'Cardiologia'),

        ('profissional.sj@elosaude.com',
         'UBS Centro de São Jerônimo (DEMO)',
         'São Jerônimo', 'MEDICO', 'CRM-RS DEMO-00004', 'Clínica geral')
) AS v (email, unidade, cidade, tipo, registro, especialidade)
JOIN usuario u        ON u.email = v.email
JOIN unidade_saude un ON un.nome = v.unidade AND un.cidade = v.cidade
WHERE NOT EXISTS (
    SELECT 1 FROM profissional_saude ps WHERE ps.usuario_id = u.id
);