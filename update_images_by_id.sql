-- 使用ID直接更新，避免字符集问题
USE daigou;

UPDATE product SET cover_image = '/api/images/skincare-1.jpg' WHERE id = 1;
UPDATE product SET cover_image = '/api/images/skincare-2.jpg' WHERE id = 2;
UPDATE product SET cover_image = '/api/images/skincare-3.jpg' WHERE id = 3;
UPDATE product SET cover_image = '/api/images/fashion-1.jpg' WHERE id = 6;
UPDATE product SET cover_image = '/api/images/fashion-2.jpg' WHERE id = 7;
UPDATE product SET cover_image = '/api/images/baby-1.jpg' WHERE id = 8;
UPDATE product SET cover_image = '/api/images/health-1.jpg' WHERE id = 9;
UPDATE product SET cover_image = '/api/images/health-2.jpg' WHERE id = 10;
UPDATE product SET cover_image = '/api/images/shoes-1.jpg' WHERE id = 11;

-- 验证结果
SELECT id, name, cover_image FROM product;
