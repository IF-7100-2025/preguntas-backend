ALTER TABLE quiz ADD COLUMN created_by VARCHAR(36) NOT NULL;
ALTER TABLE question ADD COLUMN created_by VARCHAR(36) NOT NULL;

ALTER TABLE quiz ADD CONSTRAINT fk_quiz_created_by
    FOREIGN KEY (created_by) REFERENCES users(id_user);

ALTER TABLE question ADD CONSTRAINT fk_question_created_by
    FOREIGN KEY (created_by) REFERENCES users(id_user);