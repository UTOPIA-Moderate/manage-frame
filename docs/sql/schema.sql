-- manage-frame 数据库初始化脚本
-- 使用 PostgreSQL Schema 隔离各微服务数据
-- 数据库: manage_frame
-- Schema: auth / system / job / file

-- 创建数据库
CREATE DATABASE manage_frame;

-- 连接到数据库后执行以下脚本

-- =============================================
-- Schema 创建
-- =============================================

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS system;
CREATE SCHEMA IF NOT EXISTS job;
CREATE SCHEMA IF NOT EXISTS file;

-- =============================================
-- Auth Schema (认证服务)
-- 用户表、角色表、用户角色关联、角色菜单关联
-- =============================================

CREATE TABLE auth.sys_user (
    user_id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    username         VARCHAR(50) NOT NULL UNIQUE,
    password         VARCHAR(200) NOT NULL,
    nickname         VARCHAR(50),
    cn_name          VARCHAR(50) NOT NULL,
    en_name          VARCHAR(50),
    age              INTEGER NOT NULL DEFAULT 18,
    email            VARCHAR(100),
    phone            VARCHAR(20) NOT NULL,
    avatar_url       VARCHAR(500),
    sex              VARCHAR(1) NOT NULL DEFAULT '1',
    sort             INTEGER NOT NULL DEFAULT 99,
    status           INTEGER NOT NULL DEFAULT 1,
    motto            VARCHAR(200),
    tags             JSONB,
    city             JSONB,
    address          VARCHAR(200),
    role_id          VARCHAR(36),
    founder          VARCHAR(36) NOT NULL,
    login_num        INTEGER NOT NULL DEFAULT 0,
    login_last_ip    VARCHAR(50),
    login_last_time  TIMESTAMP,
    created_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth.sys_role (
    role_id      VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    role_name    VARCHAR(50) NOT NULL,
    role_code    VARCHAR(50) NOT NULL UNIQUE,
    describe     VARCHAR(200),
    founder      VARCHAR(36) NOT NULL,
    sort         INTEGER NOT NULL DEFAULT 0,
    status       INTEGER NOT NULL DEFAULT 1,
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth.sys_user_role (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE auth.sys_role_menu (
    role_id      VARCHAR(36) NOT NULL,
    menu_id      VARCHAR(36) NOT NULL,
    permission   VARCHAR(100),
    PRIMARY KEY (role_id, menu_id, permission)
);

-- =============================================
-- System Schema (系统服务)
-- 菜单表、操作日志表
-- =============================================

CREATE TABLE system.sys_menu (
    menu_id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name                 VARCHAR(100) NOT NULL,
    parent_id            VARCHAR(36),
    menu_type           VARCHAR(10) NOT NULL DEFAULT 'menu',
    path                 VARCHAR(200),
    icon                 VARCHAR(100),
    component            VARCHAR(200),
    redirect             VARCHAR(200),
    target               VARCHAR(20),
    permission           VARCHAR(100),
    layout               VARCHAR(20),
    nav_theme            VARCHAR(20),
    header_theme         VARCHAR(20),
    hide_children_in_menu INTEGER DEFAULT 0,
    hide_in_menu         INTEGER DEFAULT 0,
    hide_in_breadcrumb   INTEGER DEFAULT 0,
    header_render        INTEGER DEFAULT 1,
    footer_render        INTEGER DEFAULT 0,
    menu_render          INTEGER DEFAULT 1,
    flat_menu            INTEGER DEFAULT 0,
    fixed_header         INTEGER DEFAULT 0,
    fix_siderbar         INTEGER DEFAULT 0,
    founder              VARCHAR(36) NOT NULL,
    sort                 INTEGER NOT NULL DEFAULT 0,
    status               INTEGER NOT NULL DEFAULT 1,
    created_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system.sys_oper_log (
    log_id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id         VARCHAR(36),
    user_name       VARCHAR(50),
    module          VARCHAR(100),
    business_type   VARCHAR(50),
    method          VARCHAR(200),
    request_method  VARCHAR(10),
    operator_type   VARCHAR(20),
    request_url     VARCHAR(500),
    request_param   TEXT,
    response_data   TEXT,
    status          INTEGER NOT NULL DEFAULT 0,
    error_msg       TEXT,
    ip              VARCHAR(50),
    location        VARCHAR(200),
    operate_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- Job Schema (定时任务服务)
-- 定时任务表、任务日志表
-- =============================================

CREATE TABLE job.sys_job (
    job_id            VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    job_name          VARCHAR(100) NOT NULL,
    job_group         VARCHAR(50) NOT NULL DEFAULT 'default',
    job_handler       VARCHAR(200) NOT NULL,
    cron_expression   VARCHAR(100),
    misfire_policy   INTEGER NOT NULL DEFAULT 0,
    status            INTEGER NOT NULL DEFAULT 0,
    remark           VARCHAR(500),
    created_time      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job.sys_job_log (
    log_id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    job_id          VARCHAR(36) NOT NULL,
    job_name        VARCHAR(100),
    job_group       VARCHAR(50),
    handler_name    VARCHAR(200),
    executor_params TEXT,
    executor_time   INTEGER,
    status          INTEGER NOT NULL DEFAULT 0,
    error_msg       TEXT,
    execute_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- File Schema (文件服务)
-- 文件记录表
-- =============================================

CREATE TABLE file.sys_file (
    file_id        VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    file_name      VARCHAR(200) NOT NULL,
    original_name  VARCHAR(200),
    file_size      BIGINT,
    file_type      VARCHAR(50),
    file_path      VARCHAR(500) NOT NULL,
    storage_type   VARCHAR(20) NOT NULL,
    bucket_name    VARCHAR(100),
    file_url       VARCHAR(500),
    create_by      VARCHAR(50),
    created_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 初始化数据
-- =============================================

-- 创建超级管理员角色
INSERT INTO auth.sys_role (role_id, role_name, role_code, describe, founder, sort, status)
VALUES ('1', '超级管理员', 'admin', '系统最高权限', '0', 99, 1);

-- 创建默认用户 (密码: 123456, BCrypt加密)
INSERT INTO auth.sys_user (user_id, username, password, cn_name, phone, role_id, founder, status)
VALUES ('1', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '13800138000', '1', '0', 1);

-- 创建基础菜单
INSERT INTO system.sys_menu (menu_id, name, parent_id, menu_type, path, icon, permission, sort, status, founder) VALUES
('1', 'dashboard', NULL, 'dir', '/dashboard', 'icon-dashboard', 'dashboard', 99, 1, '0'),
('2', 'system', NULL, 'dir', '/system', 'icon-setting', 'system', 90, 1, '0'),
('3', 'work-bench', '1', 'menu', '/dashboard/work-bench', 'icon-work-bench', 'dashboard:work-bench', 1, 1, '0'),
('4', 'user-management', '2', 'menu', '/system/user-management', 'icon-user', 'system:user-management', 1, 1, '0'),
('5', 'role-management', '2', 'menu', '/system/role-management', 'icon-role', 'system:role-management', 2, 1, '0'),
('6', 'menu-management', '2', 'menu', '/system/menu-management', 'icon-menu', 'system:menu-management', 3, 1, '0'),
('7', 'operation-log', '2', 'menu', '/system/operation-log', 'icon-log', 'system:operation-log', 4, 1, '0');

-- 给管理员角色分配菜单权限
INSERT INTO auth.sys_role_menu (role_id, menu_id, permission) VALUES
('1', '1', 'dashboard'),
('1', '2', 'system'),
('1', '3', 'dashboard:work-bench'),
('1', '4', 'system:user-management'),
('1', '5', 'system:role-management'),
('1', '6', 'system:menu-management'),
('1', '7', 'system:operation-log');

-- 关联管理员用户和角色
INSERT INTO auth.sys_user_role (user_id, role_id) VALUES ('1', '1');

-- =============================================
-- 注释
-- =============================================

COMMENT ON TABLE auth.sys_user IS '用户表';
COMMENT ON TABLE auth.sys_role IS '角色表';
COMMENT ON TABLE auth.sys_user_role IS '用户角色关联表';
COMMENT ON TABLE auth.sys_role_menu IS '角色菜单关联表';
COMMENT ON TABLE system.sys_menu IS '菜单表';
COMMENT ON TABLE system.sys_oper_log IS '操作日志表';
COMMENT ON TABLE job.sys_job IS '定时任务表';
COMMENT ON TABLE job.sys_job_log IS '任务日志表';
COMMENT ON TABLE file.sys_file IS '文件记录表';
