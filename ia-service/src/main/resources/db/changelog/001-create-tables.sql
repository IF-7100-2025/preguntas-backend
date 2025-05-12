
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE category_embedding (
    id SERIAL PRIMARY KEY,
    category_id INT NOT NULL REFERENCES category(id),
    embedding FLOAT8[] NOT NULL
);
