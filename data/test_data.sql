-- Constructeurs
INSERT INTO Manufacturer (id, isactive, name)
VALUES (1, true, 'Airbus'),
       (2, true, 'Boeing');

-- Modèles
INSERT INTO Model (id, countbusinessslots, countecoslots, isactive, name, manufacturer_id)
VALUES (1, 300, 150, true, 'A380', 1),
       (2, 200, 90, true, 'A320', 1),
       (3, 300, 150, true, '745', 2),
       (4, 200, 90, true, '735', 2);

-- Avions
INSERT INTO Plane (arn, isactive, isundermaintenance, model_id)
VALUES ('FR-76H', true, false, 1),
       ('FR-78A', true, false, 2),
       ('ES-45N', true, false, 1),
       ('US-15C', true, false, 3),
       ('IT-961', true, false, 3),
       ('GER-78', true, false, 2),
       ('PL-12V', true, false, 4);

-- Vols
INSERT INTO Flight (id, arrivalcity, departurecity)
VALUES (1, 'New York City', 'Paris'),
       (2, 'Paris', 'New York City'),
       (3, 'Bruxelles', 'Madrid'),
       (4, 'Madrid', 'Bruxelles'),
       (5, 'Lesquin', 'Berlin'),
       (6, 'Berlin', 'Lesquin'),
       (7, 'Boston', 'Londres'),
       (8, 'Londres', 'Boston');

-- Détails de vol
INSERT INTO FlightDetails (id, arrivaledatetime, departuredatetime, flight_id, plane_arn)
VALUES (1, '2019-07-10 14:00:00', '2019-07-09 05:00:00', 1, 'FR-76H'),
       (2, '2019-07-11 15:30:00', '2019-07-10 06:30:00', 2, 'FR-76H'),
       (3, '2019-07-11 09:00:00', '2019-07-11 17:35:00', 5, 'GER-78'),
       (4, '2019-07-13 10:00:00', '2019-07-13 18:35:00', 6, 'GER-78'),
       (5, '2019-07-19 06:10:00', '2019-07-18 18:35:00', 7, 'US-15C'),
       (6, '2019-07-23 06:10:00', '2019-07-22 18:35:00', 8, 'US-15C');

-- Passagers
INSERT INTO passenger (id, idnumber, birthday, email, firstname, gender, hash, isactive, lastname, phonenumber)
VALUES (1, '4571262359', '1986-06-28', 'dupont.jean@gmail.com', 'Jean', 'M', 'hash', 'true', 'Dupont', '0645178236'),
       (2, '4571262358', '1991-04-03', 'vanbruggen.gerard@outlook.com', 'Gerard', 'M', 'hash', 'true', 'Vanbruggen',
        '082688218'),
       (3, '4571262357', '1972-01-14', 'iglesias.ingrid@yahoo.com', 'Ingrid', 'F', 'hash', 'true', 'Inglesias',
        '0245752678'),
       (4, '4571262356', '1945-09-11', 'decoco.dominic@gmail.com', 'Decoco', 'M', 'hash', 'true', 'Dominic',
        '0245752677');