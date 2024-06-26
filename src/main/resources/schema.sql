DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

DROP TABLE IF EXISTS item_requests CASCADE;
CREATE TABLE IF NOT EXISTS item_requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR NOT NULL,
  requestor_id BIGINT REFERENCES users(id),
  created TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_item_requests PRIMARY KEY (id)
);

DROP table IF EXISTS items CASCADE;
CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available BOOL NOT NULL,
  owner_id BIGINT REFERENCES users(id),
  request_id BIGINT REFERENCES item_requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);

DROP TABLE IF EXISTS bookings CASCADE;
CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT REFERENCES items(id),
  booker_id BIGINT REFERENCES users(id),
  status VARCHAR,
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

DROP TABLE IF EXISTS comments CASCADE;
CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR NOT NULL,
  item_id BIGINT REFERENCES items(id),
  author_id BIGINT REFERENCES users(id),
  created TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);