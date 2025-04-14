CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE questions (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL CHECK (LENGTH(text) <= 210),
    image BYTEA,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE answer_options (
    id SERIAL PRIMARY KEY,
    question_id INT NOT NULL REFERENCES questions(id),
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE question_category (
    question_id INT NOT NULL REFERENCES questions(id),
    category_id INT NOT NULL REFERENCES categories(id),
    PRIMARY KEY (question_id, category_id)
);
-- TODO:  falta la tabla de el usuario-pregunta (que es una relacion 1-n
-- TODO:  falta la tabla de quiz-user (que es una relacion 1-n
-- TODO:  falta la tabla de historial de quizzes (que tiene preguntas, respuestas, puntuacion, etc
-- cada pregunta es parte de uno  o mas quizze
-- cada quiz es parte de uno o mas usuario
-- cada usuario puede tener cero, uno o mas quizze
-- cada pregunta tiene una o mas respuestas(al menos una y no todas correctas
-- cada respuesta tiene una o mas opcione

