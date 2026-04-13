# 海外物品代购系统

> 基于 SpringBoot + Vue3 前后端分离架构的海外物品代购电商平台

[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.7.18-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-blue.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.0+-purple.svg)](https://vitejs.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange.svg)](https://www.mysql.com/)

---

## 📋 目录

- [项目简介](#-项目简介)
- [系统架构](#-系统架构)
- [技术栈](#-技术栈)
- [功能模块](#-功能模块)
- [快速开始](#-快速开始)
- [项目结构](#-项目结构)
- [API接口文档](#-api接口文档)
- [常见问题](#-常见问题)
- [开发计划](#-开发计划)
- [作者信息](#-作者信息)

---

## 🌟 项目简介

本系统是一个面向海外代购业务的B2C电商平台，采用前后端分离架构开发。系统支持三种用户角色，提供完整的电商业务流程，包括商品展示、购物车、订单管理、物流跟踪等核心功能。

### 核心特性

- 🔐 **JWT 认证鉴权** - 基于 Token 的无状态认证机制
- 🎨 **响应式 UI 设计** - 适配桌面端和移动端
- 📊 **多角色权限管理** - 买家、商家、管理员三级权限
- 🚀 **前后端分离** - 后端提供 RESTful API，前端独立部署
- 💾 **数据持久化** - MySQL 关系型数据库存储

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Client)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   用户前台    │  │  商家后台    │  │  管理后台    │       │
│  │  Vue3 + Vite │  │  Vue3 + Vite │  │  Vue3 + Vite │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/RESTful API
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                        后端层 (Server)                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              SpringBoot 2.7.18                      │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌─────────┐│    │
│  │  │Controller│ │  Service │ │  Mapper  │ │  Entity ││    │
│  │  └──────────┘ └──────────┘ └──────────┘ └─────────┘│    │
│  └─────────────────────────────────────────────────────┘    │
│                         │                                   │
│  ┌──────────────────────┼──────────────────────┐           │
│  │         JWT 认证      │      异常处理        │           │
│  │         CORS 跨域     │      参数校验        │           │
│  └──────────────────────┴──────────────────────┘           │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
┌────────────────┐ ┌──────────┐ ┌──────────────┐
│     MySQL      │ │  Redis   │ │  本地存储     │
│   (主数据库)    │ │  (可选)  │ │  (文件上传)  │
└────────────────┘ └──────────┘ └──────────────┘
```

---

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 核心框架 |
| Spring Security | 5.7.x | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| JWT | 0.9.1 | Token 认证 |
| Lombok | 1.18.x | 代码简化 |
| JAXB | 2.3.1 | Java 17 兼容依赖 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 渐进式框架 |
| Vite | 5.0+ | 构建工具 |
| Element Plus | 2.5+ | UI 组件库 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.6+ | HTTP 客户端 |
| TailwindCSS | 3.x | CSS 框架 |

---

## 📦 功能模块

### 1. 前台功能（普通用户）

| 模块 | 功能描述 |
|------|----------|
| 🔐 用户中心 | 注册、登录、个人信息管理、密码修改 |
| 🏠 首页 | 轮播图、商品分类、热销推荐、最新商品 |
| 🔍 商品浏览 | 商品搜索、分类筛选、商品详情、商品评价 |
| 🛒 购物车 | 添加商品、数量修改、全选结算、删除商品 |
| 📦 订单管理 | 创建订单、订单列表、订单详情、确认收货 |
| 📍 地址管理 | 添加/编辑/删除收货地址、设置默认地址 |
| ⭐ 评价管理 | 发表评价、查看评价、评价回复 |
| ❤️ 收藏管理 | 收藏商品、取消收藏、收藏列表 |

### 2. 商家后台

| 模块 | 功能描述 |
|------|----------|
| 🏪 店铺管理 | 店铺信息设置、资质认证 |
| 📋 商品管理 | 发布商品、商品上下架、商品编辑、库存管理 |
| 📦 订单处理 | 订单列表、发货处理、物流更新 |
| 💬 评价管理 | 查看评价、回复评价 |
| 📊 数据统计 | 销售额统计、订单统计、商品销量排行 |

### 3. 管理后台

| 模块 | 功能描述 |
|------|----------|
| 📊 数据概览 | 用户数量、订单数量、销售额等核心指标 |
| 👥 用户管理 | 用户列表、用户状态管理 |
| 🏪 商家管理 | 商家入驻审核、商家列表、商家状态管理 |
| 📁 分类管理 | 商品分类的增删改查、分类排序 |
| 📢 公告管理 | 系统公告发布、编辑、删除 |
| 🔐 权限管理 | 角色管理、权限分配 |

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+（本项目使用 Java 17）
- **MySQL**: 8.0+ 或 MariaDB 10.4+
- **Node.js**: 18+
- **Maven**: 3.6+（或使用 Maven Wrapper）
- **Redis**: 可选，用于缓存（未安装不影响基础功能）

### 1. 数据库初始化

```bash
# 登录 MySQL（根据你的配置调整密码）
mysql -u root -p

# 在 MySQL 控制台中执行：
CREATE DATABASE IF NOT EXISTS daigou 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_general_ci;

USE daigou;

source sql/daigou.sql;

# 验证数据
SELECT username, nickname, role FROM user;
```

### 2. 启动后端服务

```bash
cd server

# 方式一：使用 Maven
mvn clean compile spring-boot:run

# 方式二：使用 Maven Wrapper
./mvnw clean compile spring-boot:run
```

后端服务启动成功后：
- 服务地址：http://localhost:9090
- API 文档：http://localhost:9090/swagger-ui.html（如配置了 Swagger）

### 3. 启动前端服务

```bash
cd client

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务启动成功后：
- 访问地址：http://localhost:8080
- 系统会自动打开浏览器

### 4. 访问系统

| 入口 | 地址 | 说明 |
|------|------|------|
| 用户前台 | http://localhost:8080 | 普通用户访问 |
| 商家后台 | http://localhost:8080/merchant | 商家角色访问 |
| 管理后台 | http://localhost:8080/admin | 管理员访问 |

---

## 👤 默认测试账号

> ⚠️ 提示：首次启动系统后，请使用以下账号登录测试

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 拥有系统所有权限 |
| 普通用户 | xiaoming | user123 | 普通买家账号 |
| 商家 | zhangsan | user123 | 已入驻商家账号 |
| 普通用户 | lily | user123 | 普通买家账号 |

---

## 📁 项目结构

```
基于 SpringBoot+Vue 的海外物品代购系统/
├── 📁 client/                          # 前端项目
│   ├── 📁 src/
│   │   ├── 📁 api/                     # API 接口定义
│   │   │   ├── auth.js                 # 认证相关接口
│   │   │   ├── user.js                 # 用户相关接口
│   │   │   ├── product.js              # 商品相关接口
│   │   │   └── ...
│   │   ├── 📁 components/              # 公共组件
│   │   ├── 📁 views/                   # 页面视图
│   │   │   ├── 📁 frontend/            # 前台页面
│   │   │   │   ├── Home.vue            # 首页
│   │   │   │   ├── Login.vue           # 登录页
│   │   │   │   ├── ProductList.vue     # 商品列表
│   │   │   │   └── ...
│   │   │   ├── 📁 merchant/            # 商家后台
│   │   │   └── 📁 admin/               # 管理后台
│   │   ├── 📁 store/                   # Pinia 状态管理
│   │   ├── 📁 router/                  # 路由配置
│   │   ├── 📁 utils/                   # 工具函数
│   │   ├── App.vue                     # 根组件
│   │   └── main.js                     # 入口文件
│   ├── 📄 vite.config.js               # Vite 配置
│   └── 📄 package.json                 # 依赖配置
│
├── 📁 server/                          # 后端项目
│   ├── 📁 src/main/java/com/daigou/
│   │   ├── 📁 config/                  # 配置类
│   │   │   ├── CorsConfig.java         # 跨域配置
│   │   │   ├── JwtConfig.java          # JWT 配置
│   │   │   ├── JwtInterceptor.java     # JWT 拦截器
│   │   │   ├── SecurityConfig.java     # Security 配置
│   │   │   └── WebMvcConfig.java       # Web 配置
│   │   ├── 📁 controller/              # 控制器层
│   │   │   ├── AuthController.java     # 认证接口
│   │   │   ├── UserController.java     # 用户接口
│   │   │   ├── ProductController.java  # 商品接口
│   │   │   └── ...
│   │   ├── 📁 service/                 # 业务层
│   │   │   ├── impl/                   # 实现类
│   │   ├── 📁 mapper/                  # 数据访问层
│   │   ├── 📁 entity/                  # 实体类
│   │   ├── 📁 common/                  # 通用类
│   │   │   ├── Result.java             # 统一响应封装
│   │   │   └── BusinessException.java  # 业务异常
│   │   ├── 📁 exception/               # 异常处理
│   │   └── DaigouApplication.java      # 启动类
│   ├── 📁 src/main/resources/
│   │   ├── 📄 application.yml          # 应用配置
│   │   └── 📁 mapper/                  # XML 映射文件
│   └── 📄 pom.xml                      # Maven 配置
│
├── 📁 sql/                             # 数据库脚本
│   └── 📄 daigou.sql                   # 数据库初始化脚本
│
└── 📄 README.md                        # 项目说明文档
```

---

## 📚 API 接口文档

### 接口规范

- **Base URL**: `http://localhost:9090`
- **请求格式**: JSON
- **响应格式**: JSON
- **认证方式**: Bearer Token

### 响应格式

```json
{
  "code": 200,        // 状态码：200-成功，500-失败，401-未登录
  "msg": "操作成功",   // 提示信息
  "data": {}          // 业务数据
}
```

### 主要接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| /auth/login | POST | 用户登录 |
| /auth/register | POST | 用户注册 |
| /user/info | GET | 获取用户信息 |
| /product/list | GET | 商品列表 |
| /product/{id} | GET | 商品详情 |
| /order/create | POST | 创建订单 |
| /merchant/product | POST | 商家发布商品 |
| /admin/user/list | GET | 管理员获取用户列表 |

---

## ❓ 常见问题

### Q1: 后端启动报错 `NoClassDefFoundError: javax/xml/bind/DatatypeConverter`

**原因**: Java 17 移除了 JAXB 模块，而 JWT 依赖它。

**解决**: 已在 `pom.xml` 中添加 JAXB 依赖，重新编译即可。

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
```

### Q2: 前端登录提示"系统繁忙"

**原因**: 数据库未初始化或密码不匹配。

**解决**: 
1. 确保已执行 `daigou.sql` 初始化数据库
2. 检查数据库密码是否为明文存储（开发环境）

### Q3: 跨域错误 (CORS)

**原因**: 后端未正确配置跨域。

**解决**: 已配置 `CorsConfig.java` 和 `SecurityConfig.java` 放行跨域请求。

### Q4: 图片上传后无法显示

**原因**: 上传路径配置不正确。

**解决**: 修改 `application.yml` 中的文件上传路径：
```yaml
file:
  upload-path: D:/daigou-uploads/  # 确保此目录存在
```

---

## 📅 开发计划

- [x] 项目骨架搭建
- [x] 数据库设计与初始化
- [x] 后端核心功能开发
- [x] 前端基础架构搭建
- [x] 用户前台功能
- [x] 商家后台功能
- [x] 管理后台功能
- [x] JWT 认证鉴权
- [ ] 支付接口集成（支付宝/微信）
- [ ] 消息通知系统（短信/邮件）
- [ ] 数据导出功能（Excel/PDF）
- [ ] 系统性能优化
- [ ] 部署文档编写

---

## 👨‍💻 作者信息

- **姓名**: 吴骄栩
- **学院**: 南京工程学院 计算机工程学院
- **学号**: 202220819
- **邮箱**: [待补充]

---

## 📄 许可证

本项目仅供学习交流使用，未经授权不得用于商业用途。

---

## 🙏 鸣谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)

---

> 💡 **提示**: 如果在部署或使用过程中遇到问题，请检查：
> 1. JDK 版本是否为 17+
> 2. MySQL 数据库是否已正确初始化
> 3. 前后端服务是否都已正常启动
> 4. 配置文件中的数据库连接信息是否正确
