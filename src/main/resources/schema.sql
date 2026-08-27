-- ============================================================================
-- CORPORATE BANKING DEMO - MONOLITHIC DATABASE SCHEMA
-- Single H2 database containing all three logical schemas:
--   1) RBC_*         -> owned by the rbc-access library
--   2) AUDIT_TRAIL   -> owned by the audit-trail library
--   3) CUSTOMER / BENEFICIARY / FUND_TRANSFER / APPROVAL -> owned by this app
-- ============================================================================

-- --------------------------------------------------------------------------
-- 1) RBC (Role-Based Control) tables
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS RBC_USER (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    status      VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS RBC_ROLE (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code   VARCHAR(50)  NOT NULL UNIQUE,
    role_name   VARCHAR(150) NOT NULL,
    role_type   VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS RBC_PERMISSION (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code   VARCHAR(80)  NOT NULL UNIQUE,
    permission_name   VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS RBC_USER_ROLE (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT NOT NULL,
    role_id  BIGINT NOT NULL,
    CONSTRAINT fk_uro_user FOREIGN KEY (user_id) REFERENCES RBC_USER(id),
    CONSTRAINT fk_uro_role FOREIGN KEY (role_id) REFERENCES RBC_ROLE(id)
);

CREATE TABLE IF NOT EXISTS RBC_ROLE_PERMISSION (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id        BIGINT NOT NULL,
    permission_id  BIGINT NOT NULL,
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES RBC_ROLE(id),
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES RBC_PERMISSION(id)
);

CREATE TABLE IF NOT EXISTS RBC_PERMISSION_LIMIT (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id        BIGINT NOT NULL,
    permission_id  BIGINT NOT NULL,
    max_amount     DECIMAL(19,2) NOT NULL,
    currency       VARCHAR(10) NOT NULL,
    CONSTRAINT fk_pl_role FOREIGN KEY (role_id) REFERENCES RBC_ROLE(id),
    CONSTRAINT fk_pl_perm FOREIGN KEY (permission_id) REFERENCES RBC_PERMISSION(id)
);

CREATE TABLE IF NOT EXISTS RBC_POLICY (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_code   VARCHAR(80)  NOT NULL UNIQUE,
    policy_value  VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL
);

-- --------------------------------------------------------------------------
-- 2) AUDIT_TRAIL table
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS AUDIT_TRAIL (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id        VARCHAR(64)  NOT NULL UNIQUE,
    username        VARCHAR(100) NOT NULL,
    role            VARCHAR(50),
    action          VARCHAR(80)  NOT NULL,
    module          VARCHAR(80),
    entity_type     VARCHAR(80),
    entity_id       VARCHAR(100),
    amount          DECIMAL(19,2),
    status          VARCHAR(20)  NOT NULL,
    failure_reason  VARCHAR(255),
    old_value       TEXT,
    new_value       TEXT,
    timestamp       TIMESTAMP    NOT NULL,
    ip_address      VARCHAR(45)
);

-- --------------------------------------------------------------------------
-- 3) Corporate Banking tables
-- --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CUSTOMER (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name    VARCHAR(150) NOT NULL,
    account_number   VARCHAR(30)  NOT NULL UNIQUE,
    email            VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS BENEFICIARY (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id                 BIGINT NOT NULL,
    beneficiary_name            VARCHAR(150) NOT NULL,
    beneficiary_account_number  VARCHAR(30)  NOT NULL,
    bank_ifsc                   VARCHAR(20),
    created_by                  VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ben_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(id)
);

CREATE TABLE IF NOT EXISTS FUND_TRANSFER (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref   VARCHAR(40)  NOT NULL UNIQUE,
    beneficiary_id    BIGINT NOT NULL,
    amount            DECIMAL(19,2) NOT NULL,
    currency          VARCHAR(10)  NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    created_by        VARCHAR(100) NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    approved_by       VARCHAR(100),
    approved_at       TIMESTAMP,
    CONSTRAINT fk_ft_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES BENEFICIARY(id)
);

CREATE TABLE IF NOT EXISTS APPROVAL (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_transfer_id   BIGINT NOT NULL,
    approver           VARCHAR(100) NOT NULL,
    amount             DECIMAL(19,2) NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    decided_at         TIMESTAMP    NOT NULL,
    CONSTRAINT fk_app_transfer FOREIGN KEY (fund_transfer_id) REFERENCES FUND_TRANSFER(id)
);
