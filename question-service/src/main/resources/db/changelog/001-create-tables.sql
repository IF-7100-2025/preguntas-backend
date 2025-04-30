CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE question (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL CHECK (LENGTH(text) <= 210),
    image BYTEA,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    explanation VARCHAR(1000),
    likes_count INT,
    dislikes_count INT
);

CREATE TABLE answer_options (
    id SERIAL PRIMARY KEY,
    question_id INT NOT NULL REFERENCES question(id),
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE question_category (
    question_id INT NOT NULL REFERENCES question(id),
    category_id INT NOT NULL REFERENCES category(id),
    PRIMARY KEY (question_id, category_id)
);

