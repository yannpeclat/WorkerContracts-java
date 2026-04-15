-- V2__seed_initial_data.sql
-- Initial seed data for HR Management System
-- Created: 2024-01-15

-- Insert default admin user (password: admin123 - BCrypt hashed)
-- IMPORTANT: Change this password in production!
INSERT INTO users (id, username, password, role, enabled) VALUES
    (uuid_generate_v4(), 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', 'ROLE_ADMIN', true),
    (uuid_generate_v4(), 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', 'ROLE_USER', true);

-- Sample employee data (optional - for development)
-- INSERT INTO employees (id, name, email, cpf, birth_date, status) VALUES
--     (uuid_generate_v4(), 'John Doe', 'john.doe@example.com', '123.456.789-00', '1990-05-15', 'ACTIVE'),
--     (uuid_generate_v4(), 'Jane Smith', 'jane.smith@example.com', '987.654.321-00', '1988-08-22', 'ACTIVE');
