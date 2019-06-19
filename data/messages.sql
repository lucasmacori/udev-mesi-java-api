-- Création des langues
INSERT INTO language (code, name)
VALUES ('fr', 'Français'),
       ('en', 'English'),
       ('it', 'Italiano');

-- Génériques
INSERT INTO message (code, language_code, text)
VALUES ('is_not_an_integer', 'fr', 'n''est pas un nombre entier'),
       ('is_not_an_integer', 'en', 'is not an integer'),
       ('is_not_an_integer', 'it', 'non é un numero intero');

-- Constructeurs
INSERT INTO message (code, language_code, text)
VALUES ('invalid_constructor', 'fr', 'Le constructeur est invalide, veuillez renseigner les valeurs suivantes:'),
       ('constructor_already_exists', 'fr', 'Le constructeur existe déjà'),
       ('constructor_does_not_exist', 'fr', 'Le constructeur n''existe pas'),
       ('invalid_constructor', 'en', 'The constructor does not exist, please provide the following values:'),
       ('constructor_already_exists', 'en', 'The constructor already exists'),
       ('constructor_does_not_exist', 'en', 'The constructor does not exist'),
       ('invalid_constructor', 'it', 'Il costruttore non è valido, inserire i seguenti valori:'),
       ('constructor_already_exist', 'it', 'Il costruttore esiste già'),
       ('constructor_does_not_exist', 'it', 'Il costruttore non esiste');

-- Modèles
INSERT INTO message (code, language_code, text)
VALUES ('invalid_model', 'fr', 'Le modèle est invalide, veuillez renseigner les valeurs suivantes:'),
       ('model_already_exists', 'fr', 'Le modèle existe déjà'),
       ('model_does_not_exist', 'fr', 'Le modèle n''existe pas'),
       ('invalid_model', 'en', 'The model does not exist, please provide the following values:'),
       ('model_already_exists', 'en', 'The model already exists'),
       ('model_does_not_exist', 'en', 'The model does not exist'),
       ('invalid_model', 'it', 'Il modello non è valido, inserire i seguenti valori:'),
       ('model_already_exist', 'it', 'Il modello esiste già'),
       ('model_does_not_exist', 'it', 'Il modello non esiste');