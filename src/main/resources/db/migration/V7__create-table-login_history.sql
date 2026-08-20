CREATE TABLE IF NOT EXISTS login_history(
    id CHAR(36) PRIMARY KEY NOT NULL,
    email VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    login_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);
