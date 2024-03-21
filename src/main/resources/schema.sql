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
    );