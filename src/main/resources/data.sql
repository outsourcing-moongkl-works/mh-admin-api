
-- MASTER role을 가진 테스트 유저 생성
INSERT INTO `admins` (`id`, `admin_email`, `password`, `role`, `created_at`, `updated_at`)
VALUES
    (UUID_TO_BIN(UUID()), 'test@admin.com', 'testpassword', 'MASTER', NOW(), NOW());

-- Terms 테이블에 조건부 삽입
INSERT IGNORE INTO terms (id) VALUES (UNHEX(REPLACE('4000c0f7-0c97-4bd7-a200-0de1392f1df0', '-', '')));

-- PrivacyPolicy 테이블에 조건부 삽입
INSERT IGNORE INTO privacy_policy (id) VALUES (UNHEX(REPLACE('ce7e38e7-62f2-434e-b894-a06c1746eaf1', '-', '')));

-- AboutUs 테이블에 조건부 삽입
INSERT IGNORE INTO about_us (id) VALUES (UNHEX(REPLACE('648c4bf4-3c90-492a-bb23-600dae7a4d70', '-', '')));

-- CompanyLocation 테이블에 조건부 삽입
INSERT IGNORE INTO company_location (id) VALUES (UNHEX(REPLACE('7ddde530-4d8a-429f-bb19-405f4e74057a', '-', '')));

