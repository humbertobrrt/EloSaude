INSERT INTO unidade_saude
(nome, tipo, cnes, endereco, bairro, cidade, uf, telefone, especialidades, ativa)
VALUES
    -- UBS (porta de entrada)
    ('UBS Centro (demo)',        'UBS', NULL, 'Rua Exemplo, 100',     'Centro',      'Porto Alegre', 'RS', '(51) 3000-0001',
     'Clínica geral, Enfermagem, Pediatria, Saúde da mulher', TRUE),
    ('UBS Bairro Norte (demo)',  'UBS', NULL, 'Av. Modelo, 250',      'Zona Norte',  'Porto Alegre', 'RS', '(51) 3000-0002',
     'Clínica geral, Enfermagem, Saúde da família', TRUE),
    ('UBS Bairro Sul (demo)',    'UBS', NULL, 'Rua Teste, 75',        'Zona Sul',    'Porto Alegre', 'RS', '(51) 3000-0003',
     'Clínica geral, Enfermagem, Saúde da família', TRUE),

    -- Ambulatórios de especialidades
    ('Ambulatório de Cardiologia (demo)',  'ESPECIALIZADA', NULL, 'Rua da Saúde, 500',  'Centro',  'Porto Alegre', 'RS', '(51) 3000-0101',
     'Cardiologia, Eletrocardiograma, Hipertensão', TRUE),
    ('Ambulatório de Especialidades (demo)', 'ESPECIALIZADA', NULL, 'Av. Clínicas, 800', 'Moinhos', 'Porto Alegre', 'RS', '(51) 3000-0102',
     'Endocrinologia, Neurologia, Dermatologia, Ortopedia', TRUE),

    -- Hospitais
    ('Hospital Geral (demo)',     'HOSPITAL', NULL, 'Av. Hospitalar, 1000', 'Centro',     'Porto Alegre', 'RS', '(51) 3000-0201',
     'Emergência, Clínica médica, Cirurgia geral, Cardiologia, UTI', TRUE),
    ('Hospital Regional (demo)',  'HOSPITAL', NULL, 'Rua Regional, 2000',   'Zona Leste', 'Porto Alegre', 'RS', '(51) 3000-0202',
     'Emergência, Maternidade, Pediatria, Ortopedia', TRUE);