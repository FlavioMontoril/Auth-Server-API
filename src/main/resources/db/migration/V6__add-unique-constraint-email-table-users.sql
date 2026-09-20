-- Remove a constraint ou índice caso já exista
ALTER TABLE users DROP CONSTRAINT IF EXISTS uq_users_email;
DROP INDEX IF EXISTS uq_users_email;

-- Adiciona a constraint UNIQUE com blindagem
ALTER TABLE users 
ADD CONSTRAINT uq_users_email UNIQUE (email);