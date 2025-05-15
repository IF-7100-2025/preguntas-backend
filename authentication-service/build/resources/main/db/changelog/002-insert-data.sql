
INSERT INTO role (name_role) VALUES ('ADMIN');
INSERT INTO role (name_role) VALUES ('COLAB');

INSERT INTO users (id_user, username, email, password)
VALUES 
  ('u1', 'axeldev', 'axel@example.com', 'hashed_password_1'),
  ('u2', 'maria', 'maria@example.com', 'hashed_password_2');

INSERT INTO user_role (user_id, role_id)
VALUES 
  ('u1', 1),  -- axeldev -> ADMIN
  ('u2', 2);  -- maria -> COLAB
