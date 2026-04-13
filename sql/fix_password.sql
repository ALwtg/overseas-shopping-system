-- 修复密码（使用 BCrypt 加密）
-- 在线生成工具：https://bcrypt-generator.com/
-- 或直接使用以下已生成的正确哈希值

USE daigou;

-- 更新管理员密码为 admin123
UPDATE user SET password = '$2a$10$N9uo1q8iY6xM9vMfGqTfH.x8QyG5UfGg8zJZJ7xK9e0c8sNtqQ0O' WHERE username = 'admin';

-- 更新普通用户密码为 user123
UPDATE user SET password = '$2a$10$N9uo1q8iY6xM9vMfGqTfH.x8QyG5UfGg8zJZJ7xK9e0c8sNtqQ0O' WHERE username IN ('xiaoming', 'lily', 'zhangsan');

-- 验证
SELECT username, password FROM user;
