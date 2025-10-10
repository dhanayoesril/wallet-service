CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE users ADD COLUMN IF NOT EXISTS uuid UUID UNIQUE DEFAULT uuid_generate_v4();

ALTER TABLE users ALTER COLUMN username   DROP NOT NULL;
ALTER TABLE users ALTER COLUMN email      DROP NOT NULL;
ALTER TABLE users ALTER COLUMN password   DROP NOT NULL;
ALTER TABLE users ALTER COLUMN created_at DROP NOT NULL;
ALTER TABLE users ALTER COLUMN updated_at DROP NOT NULL;

ALTER TABLE IF EXISTS wallet RENAME TO wallets;

ALTER TABLE wallets RENAME COLUMN amount TO balance;

ALTER TABLE wallets ADD COLUMN IF NOT EXISTS uuid UUID UNIQUE DEFAULT uuid_generate_v4();
ALTER TABLE wallets ADD COLUMN IF NOT EXISTS reserved_balance NUMERIC(18,2) DEFAULT 0;

ALTER TABLE wallets ALTER COLUMN balance TYPE NUMERIC(18,2) USING balance::NUMERIC(18,2);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'wallets' AND constraint_name = 'chk_wallet_reserved_balance_nonnegative'
    ) THEN
        ALTER TABLE wallets ADD CONSTRAINT chk_wallet_reserved_balance_nonnegative CHECK (reserved_balance >= 0);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'wallets' AND constraint_name = 'chk_wallet_balance_nonnegative'
    ) THEN
        ALTER TABLE wallets ADD CONSTRAINT chk_wallet_balance_nonnegative CHECK (balance >= 0);
    END IF;
END$$;

CREATE TYPE transaction_status AS ENUM ('RESERVED', 'COMPLETED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS transaction_types (
    id   SMALLINT PRIMARY KEY,
    code VARCHAR(10),
    name VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS transactions (
    id                  BIGSERIAL PRIMARY KEY,
    uuid                UUID UNIQUE DEFAULT uuid_generate_v4(),
    payer_wallet_id     BIGINT REFERENCES wallets(id) ON DELETE CASCADE,
    payee_wallet_id     BIGINT REFERENCES wallets(id) ON DELETE CASCADE,
    amount              NUMERIC(18,2) DEFAULT 0,
    transaction_type_id SMALLINT REFERENCES transaction_types(id) ON DELETE CASCADE,
    status              transaction_status DEFAULT 'RESERVED',
    created_at          timestamptz DEFAULT now(),
    updated_at          timestamptz DEFAULT now(),
    CHECK (amount >= 0),
    CONSTRAINT chk_trx_no_self_transfer CHECK (payer_wallet_id <> payee_wallet_id)
);

DO $$
DECLARE
    c_name text;
BEGIN
    SELECT constraint_name INTO c_name
    FROM information_schema.check_constraints
    WHERE check_clause LIKE '%amount >= 0%' AND constraint_schema = 'public'
    LIMIT 1;

    IF c_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE wallets DROP CONSTRAINT %I', c_name);
    END IF;
END$$;
