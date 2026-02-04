-- Initial data for testing
INSERT INTO users (id, name, email, password_hash, phone_number, date_of_birth, active, created_at, updated_at)
VALUES
('4', 'John Doe', 'john@example.com', '$2a$10$encrypted', '+1234567890', '1990-01-15', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('5', 'Jane Smith', 'jane@example.com', '$2a$10$encrypted', '+1234567891', '1992-05-20', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('6', 'Bob Johnson', 'bob@example.com', '$2a$10$encrypted', '+1234567892', '1988-08-10', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);