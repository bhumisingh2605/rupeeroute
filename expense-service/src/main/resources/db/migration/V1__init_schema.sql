CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
                       id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name       VARCHAR(100) NOT NULL,
                       upi_id     VARCHAR(50),
                       phone      VARCHAR(15) UNIQUE,
                       created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE groups (
                        id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name       VARCHAR(100) NOT NULL,
                        created_by UUID REFERENCES users(id),
                        created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE group_members (
                               group_id  UUID REFERENCES groups(id) ON DELETE CASCADE,
                               user_id   UUID REFERENCES users(id),
                               joined_at TIMESTAMPTZ DEFAULT now(),
                               PRIMARY KEY (group_id, user_id)
);

CREATE TABLE expenses (
                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          group_id    UUID REFERENCES groups(id),
                          paid_by     UUID REFERENCES users(id),
                          amount      NUMERIC(12,2) NOT NULL,
                          description VARCHAR(255),
                          split_type  VARCHAR(20) NOT NULL DEFAULT 'EQUAL',
                          created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE expense_splits (
                                id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                expense_id UUID REFERENCES expenses(id) ON DELETE CASCADE,
                                user_id    UUID REFERENCES users(id),
                                amount     NUMERIC(12,2) NOT NULL,
                                settled    BOOLEAN DEFAULT false
);

CREATE TABLE settlements (
                             id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             group_id   UUID REFERENCES groups(id),
                             from_user  UUID REFERENCES users(id),
                             to_user    UUID REFERENCES users(id),
                             amount     NUMERIC(12,2) NOT NULL,
                             status     VARCHAR(20) DEFAULT 'PENDING',
                             created_at TIMESTAMPTZ DEFAULT now(),
                             settled_at TIMESTAMPTZ
);

CREATE INDEX idx_expenses_group    ON expenses(group_id);
CREATE INDEX idx_splits_expense    ON expense_splits(expense_id);
CREATE INDEX idx_settlements_group ON settlements(group_id, status);