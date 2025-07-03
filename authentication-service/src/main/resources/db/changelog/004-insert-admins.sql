INSERT INTO users (id_user, username, email, password, is_verified)
VALUES
  ('4f4fba47-2d8f-4d3e-9483-9c083aa389d6', 'axel_dev', 'admin1@admin.com', '$2a$10$uq3TNydjkPsxPexHk5qfWuoE9MzIKHrQh9/ogQ7yX5RcPNalOj/Ry', true),
  ('c9d7f5c1-d207-4b4a-9064-3c823db0fc71', 'hasbulla90', 'admin2@admin.com', '$2a$10$Dvam1CQjx1KBg41eDqy0yu0En8PlPTC1jx.eYQ6Bt6YjfjCiHmjfa', true);

INSERT INTO user_role (user_id, role_id)
VALUES
  ('4f4fba47-2d8f-4d3e-9483-9c083aa389d6', 1),
  ('c9d7f5c1-d207-4b4a-9064-3c823db0fc71', 1);