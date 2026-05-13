-- src/test/resources/data-test.sql
-- Jeu de données pour les tests d'intégration

INSERT INTO appointment (first_name, last_name, speciality, hospital, entrance_date, leaving_date)
VALUES
    -- Cas nominal : CARDIO / CHU Lyon, plage 2024-06-15 → 2024-06-18
    ('Jean',    'Dupont',  'CARDIO',  'CHU Lyon',            '2024-06-15', '2024-06-18'),
    ('Marie',   'Martin',  'CARDIO',  'CHU Lyon',            '2024-06-14', '2024-06-17'),

    -- Autre spécialité dans le même hôpital (ne doit pas remonter sur CARDIO)
    ('Pierre',  'Bernard', 'NEURO',   'CHU Lyon',            '2024-06-15', '2024-06-20'),

    -- Même spécialité dans un autre hôpital (ne doit pas remonter sur CHU Lyon)
    ('Sophie',  'Leclerc', 'CARDIO',  'Hôpital Saint-Louis', '2024-06-15', '2024-06-19'),

    -- Plage passée (ne doit pas remonter sur 2024-06-16)
    ('Luc',     'Morel',   'CARDIO',  'CHU Lyon',            '2024-01-10', '2024-01-15');
