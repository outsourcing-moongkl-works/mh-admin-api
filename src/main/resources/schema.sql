
-- Terms 테이블 생성
CREATE TABLE IF NOT EXISTS terms
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    terms TEXT,
    created_at DATETIME,
    updated_at DATETIME
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- PrivacyPolicy 테이블 생성
CREATE TABLE IF NOT EXISTS privacy_policy
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    privacy_policy TEXT,
    created_at DATETIME,
    updated_at DATETIME
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AboutUs 테이블 생성
CREATE TABLE IF NOT EXISTS about_us
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    about_us TEXT,
    created_at DATETIME,
    updated_at DATETIME
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CompanyLocation 테이블 생성
CREATE TABLE IF NOT EXISTS company_location
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    company_location VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Admin 테이블 생성
CREATE TABLE IF NOT EXISTS `admins` (
                                        `id` BINARY(16) NOT NULL PRIMARY KEY,
    `email` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `created_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS enterprises
(
    id BINARY(16) NOT NULL PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    `role` VARCHAR(20),
    `name` VARCHAR(50) NOT NULL,
    business_number VARCHAR(50) NOT NULL,
    corporate_number VARCHAR(50) NOT NULL,
    address VARCHAR(50) NOT NULL,
    is_approved TINYINT(1) NOT NULL,
    manager_name VARCHAR(50) NOT NULL,
    manager_phone VARCHAR(50) NOT NULL,
    manager_email VARCHAR(50) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
