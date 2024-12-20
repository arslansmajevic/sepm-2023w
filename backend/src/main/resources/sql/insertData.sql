-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data
DELETE FROM race where id < 0;
DELETE FROM participation where id < 0;
DELETE FROM tournament where id < 0;
DELETE FROM horse WHERE id < 0;
DELETE FROM breed WHERE id < 0;

INSERT INTO breed (id, name)
VALUES
    (-1, 'Andalusian'),
    (-2, 'Appaloosa'),
    (-3, 'Arabian'),
    (-4, 'Belgian Draft'),
    (-5, 'Connemara Pony'),
    (-6, 'Dartmoor Pony'),
    (-7, 'Friesian'),
    (-8, 'Haflinger'),
    (-9, 'Hanoverian'),
    (-10, 'Icelandic Horse'),
    (-11, 'Lipizzaner'),
    (-12, 'Oldenburg'),
    (-13, 'Paint Horse'),
    (-14, 'Quarter Horse'),
    (-15, 'Shetland Pony'),
    (-16, 'Tinker'),
    (-17, 'Trakehner'),
    (-18, 'Warmblood'),
    (-19, 'Welsh Cob'),
    (-20, 'Welsh Pony');

-- Füge Pferdedaten hinzu
INSERT INTO horse (id, name, sex, date_of_birth, height, weight, breed_id)
VALUES
    (-1, 'Wendy', 'FEMALE', '2019-08-05', 1.40, 380, -15),
    (-2, 'Hugo', 'MALE', '2020-02-20', 1.20, 320, -20),
    (-3, 'Bella', 'FEMALE', '2005-04-08', 1.45, 550, -1),
    (-4, 'Thunder', 'MALE', '2008-07-15', 1.60, 600, -2),
    (-5, 'Luna', 'FEMALE', '2012-11-22', 1.65, 650, -3),
    (-6, 'Apollo', 'MALE', '2003-09-03', 1.52, 500, -4),
    (-7, 'Sophie', 'FEMALE', '2010-06-18', 1.70, 700, -5),
    (-8, 'Max', 'MALE', '2006-03-27', 1.55, 580, -6),
    (-9, 'Bella', 'FEMALE', '2002-08-09', 1.48, 520, -7),
    (-10, 'Rocky', 'MALE', '2013-05-05', 1.55, 620, -8),
    (-11, 'Daisy', 'FEMALE', '2007-02-12', 1.30, 350, -9),
    (-12, 'Charlie', 'MALE', '2011-09-21', 1.68, 680, -10),
    (-13, 'Ruby', 'FEMALE', '2004-07-30', 1.10, 280, -11),
    (-14, 'Duke', 'MALE', '2009-03-14', 1.75, 800, -12),
    (-15, 'Rosie', 'FEMALE', '2001-12-08', 1.57, 590, -13),
    (-16, 'Jack', 'MALE', '2014-10-25', 1.52, 560, -14),
    (-17, 'Lilly', 'FEMALE', '2008-06-03', 1.40, 400, -15),
    (-18, 'Sam', 'MALE', '2010-01-17', 1.65, 650, -16),
    (-19, 'Misty', 'FEMALE', '2005-11-09', 1.25, 320, -17),
    (-20, 'Max', 'MALE', '2012-08-29', 1.72, 670, -18),
    (-21, 'Bella', 'FEMALE', '2003-07-06', 1.50, 580, -19),
    (-22, 'Rocky', 'MALE', '2007-04-12', 1.40, 450, -1),
    (-23, 'Misty', 'FEMALE', '2015-03-12', 1.32, 360, -7),
    (-24, 'Rocky', 'MALE', '2018-08-19', 1.42, 480, -6),
    (-25, 'Lucky', 'MALE', '2019-05-25', 1.58, 620, -5),
    (-26, 'Daisy', 'FEMALE', '2017-12-01', 1.28, 340, -9),
    (-27, 'Buddy', 'MALE', '2016-09-14', 1.68, 700, -10),
    (-28, 'Molly', 'FEMALE', '2014-04-03', 1.55, 580, -13),
    (-29, 'Cody', 'MALE', '2019-11-30', 1.45, 550, -2),
    (-30, 'Rosie', 'FEMALE', '2016-06-28', 1.52, 520, -14),
    (-31, 'Leo', 'MALE', '2017-03-05', 1.70, 720, -8),
    (-32, 'Luna', 'FEMALE', '2018-10-10', 1.62, 670, -19);

INSERT INTO tournament (id, name, date_of_start, date_of_end)
VALUES
    (-1, '2023 Wien Race', '2023-08-05', '2023-11-05'),
    (-2, '2022 Sarajevo Race', '2022-05-05', '2022-05-05'),
    (-3, '2024 Berlin Race', '2024-07-15', '2024-09-15'),
    (-4, '2025 London Race', '2025-06-10', '2025-09-10'),
    (-5, '2023 Paris Race', '2023-09-20', '2023-12-20'),
    (-6, '2024 Madrid Race', '2024-08-01', '2024-11-01'),
    (-7, '2025 Rome Race', '2025-07-25', '2025-10-25'),
    (-8, '2023 New York Race', '2023-10-10', '2023-12-10'),
    (-9, '2024 Tokyo Race', '2024-11-05', '2024-12-05'),
    (-10, '2025 Sydney Race', '2025-09-15', '2025-11-15'),
    (-11, '2023 Cape Town Race', '2023-08-20', '2023-10-20'),
    (-12, '2024 Rio de Janeiro Race', '2024-07-10', '2024-09-10'),
    (-13, '2025 Moscow Race', '2025-06-15', '2025-08-15'),
    (-14, '2023 Buenos Aires Race', '2023-09-05', '2023-11-05'),
    (-15, '2024 Beijing Race', '2024-10-20', '2024-12-20'),
    (-16, '2025 Istanbul Race', '2025-08-25', '2025-10-25'),
    (-17, '2023 Dubai Race', '2023-07-15', '2023-09-15');

INSERT INTO participation (id, tournament_id, horse_id, entry)
VALUES
    (-1, -1, -1, 1),
    (-2, -1, -2, 2),
    (-3, -1, -3, 3),
    (-4, -1, -4, 4),
    (-5, -1, -5, 5),
    (-6, -1, -6, 6),
    (-7, -1, -7, 7),
    (-8, -1, -8, 8),
    (-9, -2, -1, 1),
    (-10, -2, -2, 2),
    (-11, -2, -3, 3),
    (-12, -2, -4, 4),
    (-13, -2, -5, 5),
    (-14, -2, -6, 6),
    (-15, -2, -7, 7),
    (-16, -2, -8, 8);

INSERT INTO race (id, first_place, second_place, winner, tournament_id, round)
VALUES
    (-1, -1, -2, null, -1, 1),
    (-2, -3, -4, null, -1, 1),
    (-3, -5, -6, null, -1, 1),
    (-4, -7, -8, null, -1, 1);
