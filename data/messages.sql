-- Création des langues
INSERT INTO language (code, name)
VALUES ('fr', 'Français'),
       ('en', 'English'),
       ('it', 'Italiano');

-- Génériques
INSERT INTO message (code, language_code, text)
VALUES ('is_not_an_integer', 'fr', 'n''est pas un nombre entier'),
       ('is_not_an_integer', 'en', 'is not an integer'),
       ('is_not_an_integer', 'it', 'non é un numero intero'),
       ('is_not_a_boolean', 'fr', 'n''est pas un booléen'),
       ('is_not_a_boolean', 'en', 'is not a boolean'),
       ('is_not_a_boolean', 'it', 'non é un booleano');

-- Constructeurs
INSERT INTO message (code, language_code, text)
VALUES ('invalid_constructor', 'fr', 'Le constructeur est invalide, veuillez renseigner les valeurs suivantes:'),
       ('constructor_already_exists', 'fr', 'Le constructeur existe déjà'),
       ('constructor_does_not_exist', 'fr', 'Le constructeur n''existe pas'),
       ('invalid_constructor', 'en', 'The constructor is invalid, please provide the following values:'),
       ('constructor_already_exists', 'en', 'The constructor already exists'),
       ('constructor_does_not_exist', 'en', 'The constructor does not exist'),
       ('invalid_constructor', 'it', 'Il costruttore non è valido, inserire i seguenti valori:'),
       ('constructor_already_exists', 'it', 'Il costruttore esiste già'),
       ('constructor_does_not_exist', 'it', 'Il costruttore non esiste');

-- Modèles
INSERT INTO message (code, language_code, text)
VALUES ('invalid_model', 'fr', 'Le modèle est invalide, veuillez renseigner les valeurs suivantes:'),
       ('model_already_exists', 'fr', 'Le modèle existe déjà'),
       ('model_does_not_exist', 'fr', 'Le modèle n''existe pas'),
       ('invalid_model', 'en', 'The model is invalid, please provide the following values:'),
       ('model_already_exists', 'en', 'The model already exists'),
       ('model_does_not_exist', 'en', 'The model does not exist'),
       ('invalid_model', 'it', 'Il modello non è valido, inserire i seguenti valori:'),
       ('model_already_exists', 'it', 'Il modello esiste già'),
       ('model_does_not_exist', 'it', 'Il modello non esiste');

-- Avions
INSERT INTO message (code, language_code, text)
VALUES ('invalid_plane', 'fr', 'L''avion est invalide, veuillez renseigner les valeurs suivantes:'),
       ('plane_already_exists', 'fr', 'L''avion existe déjà'),
       ('plane_does_not_exist', 'fr', 'L''avion n''existe pas'),
       ('invalid_plane', 'en', 'The plane is invalid, please provide the following values:'),
       ('plane_already_exists', 'en', 'The plane already exists'),
       ('plane_does_not_exist', 'en', 'The plane does not exist'),
       ('invalid_plane', 'it', 'L''aeroplano non è valido, inserire i seguenti valori:'),
       ('plane_already_exists', 'it', 'L''aeroplano esiste già'),
       ('plane_does_not_exist', 'it', 'L''aeroplano non esiste');

-- Vols
INSERT INTO message (code, language_code, text)
VALUES ('invalid_flight', 'fr', 'Le vol est invalide, veuillez renseigner les valeurs suivantes:'),
       ('flight_already_exists', 'fr', 'Le vol existe déjà'),
       ('flight_does_not_exist', 'fr', 'Le vol n''existe pas'),
       ('invalid_flight', 'en', 'The flight is invalid, please provide the following values:'),
       ('flight_already_exists', 'en', 'The flight already exists'),
       ('flight_does_not_exist', 'en', 'The flight does not exist'),
       ('invalid_flight', 'it', 'Il volo non è valido, inserire i seguenti valori:'),
       ('flight_already_exists', 'it', 'Il volo esiste già'),
       ('flight_does_not_exist', 'it', 'Il volo non esiste');