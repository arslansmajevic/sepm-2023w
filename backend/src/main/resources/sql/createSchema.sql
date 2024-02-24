CREATE TABLE IF NOT EXISTS breed
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
  sex ENUM ('MALE', 'FEMALE') NOT NULL,
  date_of_birth DATE NOT NULL,
  height NUMERIC(4,2),
  weight NUMERIC(7,2),
  breed_id BIGINT REFERENCES breed(id)
);

CREATE TABLE IF NOT EXISTS tournament
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  date_of_start DATE NOT NULL,
  date_of_end DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS participation
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tournament_id BIGINT,
  horse_id BIGINT,
  entry BIGINT,
  FOREIGN KEY (tournament_id) REFERENCES tournament(id),
  FOREIGN KEY (horse_id) REFERENCES horse(id)
);

CREATE TABLE IF NOT EXISTS race
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_place BIGINT,
  second_place BIGINT,
  winner BIGINT,
  tournament_id BIGINT,
  round BIGINT,
  FOREIGN KEY (first_place) REFERENCES horse(id),
  FOREIGN KEY (second_place) REFERENCES horse(id),
  FOREIGN KEY (winner) REFERENCES horse(id),
  FOREIGN KEY (tournament_id) REFERENCES tournament(id)
);