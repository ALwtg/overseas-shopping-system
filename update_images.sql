-- 更新所有产品图片路径为本地路径
-- 请在 MariaDB 中执行: source update_images.sql

USE daigou;

-- 美妆护肤
UPDATE product SET cover_image = '/api/images/skincare-1.jpg' WHERE name LIKE '%雅诗兰黛%';
UPDATE product SET cover_image = '/api/images/skincare-2.jpg' WHERE name LIKE '%海蓝之谜%';
UPDATE product SET cover_image = '/api/images/skincare-3.jpg' WHERE name LIKE '%SK-II%' OR name LIKE '%神仙水%';

-- 数码电子
UPDATE product SET cover_image = '/api/images/electronics-1.jpg' WHERE name LIKE '%AirPods%';
UPDATE product SET cover_image = '/api/images/electronics-2.jpg' WHERE name LIKE '%Apple Watch%';

-- 时尚服饰
UPDATE product SET cover_image = '/api/images/fashion-1.jpg' WHERE name LIKE '%加拿大鹅%';
UPDATE product SET cover_image = '/api/images/fashion-2.jpg' WHERE name LIKE '%Supreme%';

-- 母婴用品
UPDATE product SET cover_image = '/api/images/baby-1.jpg' WHERE name LIKE '%美赞臣%' OR name LIKE '%Enfamil%';

-- 食品保健
UPDATE product SET cover_image = '/api/images/health-1.jpg' WHERE name LIKE '%生命花园%' OR name LIKE '%蛋白质%';
UPDATE product SET cover_image = '/api/images/health-2.jpg' WHERE name LIKE '%Swisse%' OR name LIKE '%血橙%';

-- 运动户外
UPDATE product SET cover_image = '/api/images/shoes-1.jpg' WHERE name LIKE '%Nike%' OR name LIKE '%Jordan%';

-- 验证更新结果
SELECT id, name, cover_image FROM product;
