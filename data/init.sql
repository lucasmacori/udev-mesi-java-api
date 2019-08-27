-- Base de données et utilisateur
CREATE USER udev WITH PASSWORD 'udev';
CREATE DATABASE udev;
GRANT ALL PRIVILEGES ON DATABASE udev to udev;

-- Table
create table flight
(
    id            bigserial            not null
        constraint flight_pkey
            primary key,
    arrivalcity   varchar(75)          not null,
    departurecity varchar(75)          not null,
    isactive      boolean default true not null
);

alter table flight
    owner to udev;

create table language
(
    code varchar(5)  not null
        constraint language_pkey
            primary key,
    name varchar(25) not null
        constraint uk_nk4c9qcgv8el6abqd6etg77yy
            unique
);

alter table language
    owner to udev;

create table manufacturer
(
    id       bigserial            not null
        constraint manufacturer_pkey
            primary key,
    isactive boolean default true not null,
    name     varchar(50)          not null
        constraint uk_4dhamqcky7ui5mlxhbaoxk5mp
            unique
);

alter table manufacturer
    owner to udev;

create table message
(
    id            bigserial    not null
        constraint message_pkey
            primary key,
    code          varchar(255) not null,
    text          text         not null,
    language_code varchar(5)   not null
        constraint fkh9ar214333cuvha3cx9ttlqio
            references language
);

alter table message
    owner to udev;

create table model
(
    id                 bigserial            not null
        constraint model_pkey
            primary key,
    countbusinessslots integer default 0    not null,
    countecoslots      integer default 0    not null,
    isactive           boolean default true not null,
    name               varchar(50)          not null,
    manufacturer_id    bigint               not null
        constraint fk1ke4qk6wq2val6a7397xkjf9d
            references manufacturer
);

alter table model
    owner to udev;

create index manufacturerindex
    on model (manufacturer_id);

create index nameindex
    on model (name);

create table passenger
(
    id          bigserial            not null
        constraint passenger_pkey
            primary key,
    idnumber    varchar(20)          not null
        constraint uk_lm22lx7ydd5nfvak1etpqcgre
            unique,
    birthday    timestamp            not null,
    email       varchar(50)          not null
        constraint uk_9w8dpljkie3n242vjdfndqerf
            unique,
    firstname   varchar(35)          not null,
    gender      char                 not null,
    hash        varchar(255),
    isactive    boolean default true not null,
    lastname    varchar(40)          not null,
    phonenumber varchar(15)          not null
        constraint uk_4ougw2quw644jsh2ggm9c5hl5
            unique
);

alter table passenger
    owner to udev;

create index hashindex
    on passenger (hash);

create table plane
(
    arn                varchar(10)          not null
        constraint plane_pkey
            primary key,
    isactive           boolean default true not null,
    isundermaintenance boolean default true not null,
    model_id           bigint               not null
        constraint fkbrqjumi0vxrvtva9en7kgv5vc
            references model
);

alter table plane
    owner to udev;

create table flightdetails
(
    id                bigserial            not null
        constraint flightdetails_pkey
            primary key,
    arrivaledatetime  timestamp            not null,
    departuredatetime timestamp            not null,
    isactive          boolean default true not null,
    flight_id         bigint               not null
        constraint fkp32h83f65rb1o30hadrtmw4p
            references flight,
    plane_arn         varchar(10)          not null
        constraint fkkwbkt7hkg6wt3lm4cyclsblxw
            references plane
);

alter table flightdetails
    owner to udev;

create index flightindex
    on flightdetails (flight_id);

create index planeindex
    on flightdetails (plane_arn);

create table reservation
(
    id               bigserial               not null
        constraint reservation_pkey
            primary key,
    isactive         boolean   default true  not null,
    reservationclass char                    not null,
    reservationdate  timestamp default now() not null,
    flightdetails_id bigint                  not null
        constraint fkim3l4txbng0s3grw7vww9k00p
            references flightdetails,
    passenger_id     bigint                  not null
        constraint fkqpa0hqtnc4al5q9aruo6ylet
            references passenger
);

alter table reservation
    owner to udev;

create index flightdetailsindex
    on reservation (flightdetails_id);

create index passengerindex
    on reservation (passenger_id);

create table report
(
    code        varchar(255)         not null
        constraint report_pkey
            primary key,
    description varchar(255)         not null,
    isactive    boolean default true not null,
    query       varchar(255)         not null
);

alter table report
    owner to udev;

-- Messages
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
       ('is_not_a_boolean', 'it', 'non é un booleano'),
       ('is_not_a_date', 'fr', 'n''est pas une date'),
       ('is_not_a_date', 'en', 'is not a date'),
       ('is_not_a_date', 'it', 'non é una data'),
       ('is_not_valid', 'fr', 'n''est pas valide'),
       ('is_not_valid', 'en', 'is not valid'),
       ('is_not_valid', 'it', 'non é valido');

-- Constructeurs
INSERT INTO message (code, language_code, text)
VALUES ('invalid_manufacturer', 'fr', 'Le constructeur est invalide, veuillez renseigner les valeurs suivantes:'),
       ('manufacturer_already_exists', 'fr', 'Le constructeur existe déjà'),
       ('manufacturer_does_not_exist', 'fr', 'Le constructeur n''existe pas'),
       ('invalid_manufacturer', 'en', 'The manufacturer is invalid, please provide the following values:'),
       ('manufacturer_already_exists', 'en', 'The manufacturer already exists'),
       ('manufacturer_does_not_exist', 'en', 'The manufacturer does not exist'),
       ('invalid_manufacturer', 'it', 'Il costruttore non è valido, inserire i seguenti valori:'),
       ('manufacturer_already_exists', 'it', 'Il costruttore esiste già'),
       ('manufacturer_does_not_exist', 'it', 'Il costruttore non esiste');

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

-- Détail de vol
INSERT INTO message (code, language_code, text)
VALUES ('invalid_flight_details', 'fr', 'Le détail du vol est invalide, veuillez renseigner les valeurs suivantes:'),
       ('flight_details_already_exist', 'fr', 'Le détail du vol existe déjà'),
       ('flight_details_do_not_exist', 'fr', 'Le détail du vol n''existe pas'),
       ('invalid_flight_details', 'en', 'The flight details are invalid, please provide the following values:'),
       ('flight_details_already_exist', 'en', 'The flight details already exist'),
       ('flight_details_do_not_exist', 'en', 'The flight details do not exist'),
       ('invalid_flight_details', 'it', 'I dettagli del volo non sono validi, inserire i seguenti valori:'),
       ('flight_details_already_exist', 'it', 'I dettagli del volo esistono già'),
       ('flight_details_do_not_exist', 'it', 'I dettagli del volo non esistono');

-- Passagers
INSERT INTO message (code, language_code, text)
VALUES ('invalid_passenger', 'fr', 'Le passager est invalide, veuillez renseigner les valeurs suivantes:'),
       ('passenger_already_exists', 'fr', 'Le passager existe déjà'),
       ('passenger_does_not_exist', 'fr', 'Le passager n''existe pas'),
       ('invalid_passenger', 'en', 'The passenger is invalid, please provide the following values:'),
       ('passenger_already_exists', 'en', 'The passenger already exist'),
       ('passenger_does_not_exist', 'en', 'The passenger does not exist'),
       ('invalid_passenger', 'it', 'Il passeggero non é valido, inserire i seguenti valori:'),
       ('passenger_already_exists', 'it', 'Il passeggero esiste già'),
       ('passenger_does_not_exist', 'it', 'Il passeggero non esiste');

-- Réservations
INSERT INTO message (code, language_code, text)
VALUES ('invalid_reservation', 'fr', 'La réservation est invalide, veuillez renseigner les valeurs suivantes:'),
       ('reservation_already_exists', 'fr', 'La réservation existe déjà'),
       ('reservation_does_not_exist', 'fr', 'La réservation n''existe pas'),
       ('invalid_reservation', 'en', 'The reservation is invalid, please provide the following values:'),
       ('reservation_already_exists', 'en', 'The reservation already exist'),
       ('reservation_does_not_exist', 'en', 'The reservation does not exist'),
       ('invalid_reservation', 'it', 'La prenotazione non é valida, inserire i seguenti valori:'),
       ('reservation_already_exists', 'it', 'La prenotazione esiste già'),
       ('reservation_does_not_exist', 'it', 'La prenotazione non esiste');

-- Rapports
INSERT INTO message (code, language_code, text)
VALUES ('invalid_report', 'fr', 'Le rapport est invalide, veuillez renseigner les valeurs suivantes:'),
       ('report_already_exists', 'fr', 'Le rapport existe déjà'),
       ('report_does_not_exist', 'fr', 'Le rapport n''existe pas'),
       ('invalid_report', 'en', 'The report is invalid, please provide the following values:'),
       ('report_already_exists', 'en', 'The report already exists'),
       ('report_does_not_exist', 'en', 'The report does not exist'),
       ('invalid_report', 'it', 'Il relazione non è valido, inserire i seguenti valori:'),
       ('report_already_exists', 'it', 'Il relazione esiste già'),
       ('report_does_not_exist', 'it', 'Il relazione non esiste');

-- Rapports (requêtes SQL)
-- Réservations sur une période donnée
INSERT INTO Report (code, description, isActive, query)
VALUES ('reservations_periode', 'Récupère les réservations sur une période donnée', true,
        'SELECT COUNT(*) AS Nombre FROM Reservation WHERE reservationDate >= :minDate AND reservationDate <= :maxDate GROUP BY reservationDate ORDER BY reservationDate');

-- Passagers enregistrés dans la base
INSERT INTO Report (code, description, isActive, query)
VALUES ('nombre_passagers_enregistres', 'Récupère le nombre de passagers enregistrés', true,
        'SELECT COUNT(*) AS Nombre FROM Passenger');

-- Passagers ayant effectué une réservation dans une période donnée
INSERT INTO Report (code, description, isActive, query)
VALUES ('nombre_passagers_reservations_periode',
        'Récupère le nombre de passagers ayant effectué au moins une réservation dans une période donnée', true,
        'SELECT SUM(Compteur) AS Nombre FROM (SELECT COUNT(DISTINCT passenger_id) AS Compteur FROM Reservation WHERE reservationDate >= :minDate AND reservationDate <= :maxDate ) AS requete');

-- Nombre d'annulations sur une période donnée
INSERT INTO Report (code, description, isActive, query)
VALUES ('nombre_annulations_periode', 'Récupère le nombre d''annulations sur une période donnée', true,
        'SELECT COUNT(*) AS Nombre FROM Reservation WHERE isActive = false AND reservationDate >= :minDate AND reservationdate <= :maxDate ');