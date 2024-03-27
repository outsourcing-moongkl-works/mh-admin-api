
-- MASTER role을 가진 테스트 유저 생성
INSERT INTO `admins` (id, email, password, role, created_at, updated_at)
VALUES (UNHEX(REPLACE(UUID(), '-', '')), 'test@admin.com', '$2a$12$./wHNApeq/feNKojcfkWouvZJfPrhdbDKWXQZWZy9PaW7pnkt/ZV6', 'MASTER', NOW(), NOW())
ON DUPLICATE KEY UPDATE email = email;

-- Terms 테이블에 조건부 삽입
INSERT IGNORE INTO terms (id) VALUES (UNHEX(REPLACE('4000c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')));

-- PrivacyPolicy 테이블에 조건부 삽입
INSERT IGNORE INTO privacy_policy (id) VALUES (UNHEX(REPLACE('ce7e38e7-62f2-434e-b894-a06c1746eaf1', '-', '')));

-- AboutUs 테이블에 조건부 삽입
INSERT IGNORE INTO about_us (id) VALUES (UNHEX(REPLACE('648c4bf4-3c90-492a-bb23-600dae7a4d70', '-', '')));

-- CompanyLocation 테이블에 조건부 삽입
INSERT IGNORE INTO company_location (id) VALUES (UNHEX(REPLACE('7ddde530-4d8a-429f-bb19-405f4e74057a', '-', '')));

-- enterprises 테이블에 테스트 계정 삽입
INSERT INTO enterprises (id, login_id, password, role, name, business_number, corporate_number, address, is_approved, manager_name, manager_phone, manager_email, created_at, updated_at)
VALUES (UNHEX(REPLACE(UUID(), '-', '')), 'testid', '$2a$12$./wHNApeq/feNKojcfkWouvZJfPrhdbDKWXQZWZy9PaW7pnkt/ZV6', 'ENTERPRISE', 'Test Enterprise', '123-45-6789', '987-65-4321', 'Test Address', 'YES', 'Manager Name', '010-1234-5678', 'manager@test.com', NOW(), NOW())
ON DUPLICATE KEY UPDATE login_id = login_id;
