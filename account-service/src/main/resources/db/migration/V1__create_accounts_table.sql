CREATE TABLE IF NOT EXISTS accounts (
    id             BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    owner_name     VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    balance        NUMERIC(19,2) NOT NULL,
    status         VARCHAR(50) NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_accounts_email
    ON accounts(email);

CREATE INDEX IF NOT EXISTS idx_accounts_account_number
    ON accounts(account_number);
