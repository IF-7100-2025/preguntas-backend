CREATE TABLE users (
    id_user VARCHAR PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE role (
    id_role SERIAL PRIMARY KEY,
    name_role VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_role (
    id_user_role SERIAL PRIMARY KEY,
    user_id VARCHAR NOT NULL REFERENCES users(id_user),
    role_id INTEGER NOT NULL REFERENCES role(id_role)
);
