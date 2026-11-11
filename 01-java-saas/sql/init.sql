-- SaaS 多租户知识库数据库初始化脚本
CREATE DATABASE IF NOT EXISTS saas_kb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE saas_kb;

-- 1. 知识库表
CREATE TABLE IF NOT EXISTS kb_knowledge_base (
    id BIGINT PRIMARY KEY COMMENT '主键(雪花ID)',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    name VARCHAR(255) NOT NULL COMMENT '知识库名称',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant (tenant_id)
) COMMENT='知识库表';

-- 2. 文档表
CREATE TABLE IF NOT EXISTS kb_document (
    id BIGINT PRIMARY KEY COMMENT '主键(雪花ID)',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    kb_id BIGINT COMMENT '所属知识库ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    status TINYINT DEFAULT 1 COMMENT '1正常 0删除(逻辑删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant (tenant_id),
    INDEX idx_kb (kb_id),
    INDEX idx_tenant_kb (tenant_id, kb_id)
) COMMENT='文档表';

-- 3. 标签表(P1)
CREATE TABLE IF NOT EXISTS kb_tag (
    id BIGINT PRIMARY KEY COMMENT '主键(雪花ID)',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    name VARCHAR(64) NOT NULL COMMENT '标签名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tenant (tenant_id)
) COMMENT='标签表';

-- 4. 文档-标签关联表(P1)
CREATE TABLE IF NOT EXISTS kb_document_tag (
    document_id BIGINT NOT NULL COMMENT '文档ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (document_id, tag_id),
    INDEX idx_tag (tag_id)
) COMMENT='文档标签关联表';
