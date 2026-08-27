-- ============================================================================
-- CORPORATE BANKING DEMO - SEED DATA
-- ============================================================================

-- --------------------------------------------------------------------------
-- 1) RBC seed data - roles, permissions, users, mappings, limits, policy
-- --------------------------------------------------------------------------

-- Roles (dynamic - not hard-coded anywhere in Java code)
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('MAKER_1',   'Maker Level 1 - Beneficiary Only',        'MAKER');
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('MAKER_2',   'Maker Level 2 - Transfer up to 1000',     'MAKER');
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('MAKER_3',   'Maker Level 3 - Transfer up to 10000',    'MAKER');
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('CHECKER_1', 'Checker Level 1 - Approve up to 1000',    'CHECKER');
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('CHECKER_2', 'Checker Level 2 - Approve up to 3000',    'CHECKER');
INSERT INTO RBC_ROLE (role_code, role_name, role_type) VALUES ('STP',       'Straight Through Processing',            'STP');

-- Permissions
INSERT INTO RBC_PERMISSION (permission_code, permission_name) VALUES ('CREATE_BENEFICIARY',    'Create Beneficiary');
INSERT INTO RBC_PERMISSION (permission_code, permission_name) VALUES ('CREATE_FUND_TRANSFER',  'Create Fund Transfer');
INSERT INTO RBC_PERMISSION (permission_code, permission_name) VALUES ('APPROVE_FUND_TRANSFER', 'Approve Fund Transfer');

-- Global policy: self-approval is NOT allowed, for ANY role
INSERT INTO RBC_POLICY (policy_code, policy_value, status) VALUES ('SELF_APPROVAL_ALLOWED', 'false', 'ACTIVE');

-- Users
INSERT INTO RBC_USER (username, status) VALUES ('maker1',   'ACTIVE');
INSERT INTO RBC_USER (username, status) VALUES ('maker2',   'ACTIVE');
INSERT INTO RBC_USER (username, status) VALUES ('maker3',   'ACTIVE');
INSERT INTO RBC_USER (username, status) VALUES ('checker1', 'ACTIVE');
INSERT INTO RBC_USER (username, status) VALUES ('checker2', 'ACTIVE');
INSERT INTO RBC_USER (username, status) VALUES ('stp',      'ACTIVE');

-- User -> Role
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'maker1'   AND r.role_code = 'MAKER_1';
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'maker2'   AND r.role_code = 'MAKER_2';
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'maker3'   AND r.role_code = 'MAKER_3';
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'checker1' AND r.role_code = 'CHECKER_1';
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'checker2' AND r.role_code = 'CHECKER_2';
INSERT INTO RBC_USER_ROLE (user_id, role_id)
  SELECT u.id, r.id FROM RBC_USER u, RBC_ROLE r WHERE u.username = 'stp'      AND r.role_code = 'STP';

-- Role -> Permission
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_1'   AND p.permission_code = 'CREATE_BENEFICIARY';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_2'   AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_3'   AND p.permission_code = 'CREATE_BENEFICIARY';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_3'   AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'CHECKER_1' AND p.permission_code = 'APPROVE_FUND_TRANSFER';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'CHECKER_2' AND p.permission_code = 'APPROVE_FUND_TRANSFER';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'STP'       AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_ROLE_PERMISSION (role_id, permission_id)
  SELECT r.id, p.id FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'STP'       AND p.permission_code = 'APPROVE_FUND_TRANSFER';

-- Permission limits (maker transaction limits AND checker approval limits, same table)
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 1000, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_2'   AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 10000, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'MAKER_3'   AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 1000, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'CHECKER_1' AND p.permission_code = 'APPROVE_FUND_TRANSFER';
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 3000, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'CHECKER_2' AND p.permission_code = 'APPROVE_FUND_TRANSFER';
-- STP is granted both permissions with an effectively unlimited ceiling
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 999999999, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'STP' AND p.permission_code = 'CREATE_FUND_TRANSFER';
INSERT INTO RBC_PERMISSION_LIMIT (role_id, permission_id, max_amount, currency)
  SELECT r.id, p.id, 999999999, 'INR' FROM RBC_ROLE r, RBC_PERMISSION p WHERE r.role_code = 'STP' AND p.permission_code = 'APPROVE_FUND_TRANSFER';

-- --------------------------------------------------------------------------
-- 2) Corporate Banking seed data
-- --------------------------------------------------------------------------
INSERT INTO CUSTOMER (customer_name, account_number, email) VALUES ('Acme Exports Pvt Ltd',  'CB-ACC-0001', 'finance@acmeexports.example');
INSERT INTO CUSTOMER (customer_name, account_number, email) VALUES ('Bharat Logistics Ltd',  'CB-ACC-0002', 'accounts@bharatlogistics.example');
INSERT INTO CUSTOMER (customer_name, account_number, email) VALUES ('Chennai Textiles Corp', 'CB-ACC-0003', 'ap@chennaitextiles.example');

-- One ready-made beneficiary per customer so /api/transfers can be exercised immediately
INSERT INTO BENEFICIARY (customer_id, beneficiary_name, beneficiary_account_number, bank_ifsc, created_by)
  SELECT id, 'Global Traders Co', 'BEN-ACC-1001', 'HDFC0001234', 'seed' FROM CUSTOMER WHERE account_number = 'CB-ACC-0001';
INSERT INTO BENEFICIARY (customer_id, beneficiary_name, beneficiary_account_number, bank_ifsc, created_by)
  SELECT id, 'Metro Freight Services', 'BEN-ACC-1002', 'ICIC0005678', 'seed' FROM CUSTOMER WHERE account_number = 'CB-ACC-0002';
INSERT INTO BENEFICIARY (customer_id, beneficiary_name, beneficiary_account_number, bank_ifsc, created_by)
  SELECT id, 'Southern Yarn Suppliers', 'BEN-ACC-1003', 'SBIN0009876', 'seed' FROM CUSTOMER WHERE account_number = 'CB-ACC-0003';

-- No AUDIT_TRAIL or FUND_TRANSFER rows are seeded - they are created live as
-- you exercise the APIs, so the audit trail genuinely reflects your own demo run.
