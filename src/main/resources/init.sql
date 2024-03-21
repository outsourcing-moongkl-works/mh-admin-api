-- Users 테이블 생성
CREATE TABLE IF NOT EXISTS terms
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    terms VARCHAR(10000),
    created_at DATETIME,
    updated_at DATETIME
    );

-- Terms 테이블 생성
CREATE TABLE IF NOT EXISTS terms
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    terms VARCHAR(10000),
    created_at DATETIME,
    updated_at DATETIME
    );

-- PrivacyPolicy 테이블 생성
CREATE TABLE IF NOT EXISTS privacy_policy
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    privacy_policy VARCHAR(10000),
    created_at DATETIME,
    updated_at DATETIME
    );

-- AboutUs 테이블 생성
CREATE TABLE IF NOT EXISTS about_us
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    about_us VARCHAR(10000),
    created_at DATETIME,
    updated_at DATETIME
    );

-- CompanyLocation 테이블 생성
CREATE TABLE IF NOT EXISTS company_location
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    company_location VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME
    );

-- Admin 테이블 생성
CREATE TABLE IF NOT EXISTS `admins` (
                                        `id` BINARY(16) NOT NULL,
    `admin_email` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `created_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

