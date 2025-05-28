CREATE TABLE quiz (
    id_quiz UUID PRIMARY KEY NOT NULL,
    grade INTEGER,
    completed TIMESTAMP,
    status VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP
);

CREATE TABLE quiz_question (
    id_quiz UUID,
    question_id UUID,
    PRIMARY KEY (id_quiz, question_id),
    CONSTRAINT fk_quiz_question_quiz FOREIGN KEY (id_quiz)
        REFERENCES quiz(id_quiz) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_question_question FOREIGN KEY (question_id)
        REFERENCES question(id) ON DELETE CASCADE
);
