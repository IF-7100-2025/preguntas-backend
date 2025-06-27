CREATE TABLE question_report (
    id_report   UUID     NOT NULL PRIMARY KEY,
    id_question UUID     NOT NULL,
    id_user     VARCHAR(36)     NOT NULL,
    reason      VARCHAR(255),
    description TEXT,
    reported_at TIMESTAMP NOT NULL,
    status      VARCHAR(50),
    CONSTRAINT fk_question_report_question
        FOREIGN KEY (id_question) REFERENCES question(id),
    CONSTRAINT fk_question_report_user
        FOREIGN KEY (id_user)     REFERENCES users(id_user)
);

CREATE INDEX idx_question_report_question ON question_report(id_question);
CREATE INDEX idx_question_report_user     ON question_report(id_user);
