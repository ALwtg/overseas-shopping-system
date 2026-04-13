这是一个非常明智的选择。对于毕业设计而言，**使用 IntelliJ IDEA (简称 IDEA) 作为全栈开发工具**，可以极大地减少环境配置的复杂度，避免在不同编辑器间切换的麻烦。

IDEA 的 Ultimate 版本对 Vue、Node.js 和 SpringBoot 都有原生的完美支持。以下是为你定制的**“单机单软件”**开发方案。

### 📂 第一部分：IDEA 中的项目文件目录结构

建议采用**多模块 (Multi-Module)** 或者**聚合项目**的方式管理。为了简单起见，我们采用“聚合项目”结构，即在一个 IDEA 窗口里同时管理前后端。

在你的电脑上创建一个根文件夹，例如 `DaigouSystem`，目录结构如下：

text

```
DaigouSystem/                 <-- IDEA 打开的根目录
│
├── server/                   <-- 后端 SpringBoot 模块 (Module)
│   ├── src/                  
│   │   ├── main/             
│   │   │   ├── java/         
│   │   │   │   └── com/      
│   │   │   │       └── daigou/  <-- 你的包名
│   │   │   │           ├── DaigouApplication.java  <-- 启动类 (右键运行)
│   │   │   │           ├── controller/            <-- 接收前端请求
│   │   │   │           ├── service/               <-- 业务逻辑
│   │   │   │           ├── mapper/                <-- 数据库操作
│   │   │   │           ├── entity/                <-- 实体类
│   │   │   │           └── config/                <-- 配置类 (如JWT)
│   │   │   │
│   │   │   └── resources/    
│   │   │       ├── application.yml   <-- 核心配置
│   │   │       └── mapper/           <-- XML文件
│   │   │
│   │   └── test/         <-- 测试
│   │
│   └── pom.xml           <-- 后端依赖管理 (Maven)
│
├── client/                 <-- 前端 Vue 模块 (Module)
│   ├── public/             <-- 静态资源
│   │   └── index.html      
│   ├── src/                
│   │   ├── assets/         <-- 图片/CSS
│   │   ├── components/     <-- 公共组件
│   │   ├── views/          <-- 页面 (Home, User, Order)
│   │   ├── router/         <-- 路由配置
│   │   ├── store/          <-- 状态管理 (Pinia/Vuex)
│   │   ├── api/            <-- 关键：封装的后端接口调用
│   │   ├── main.js         <-- 入口文件
│   │   └── App.vue         <-- 根组件
│   │
│   └── package.json        <-- 前端依赖管理 (Node.js)
│
├── docs/                   <-- 文档 (存放你的开题报告、数据库设计图)
│   ├── 开题报告.docx
│   └── database_design.png
│
└── README.md               <-- 项目说明 (如何运行)
```

### ⚙️ 第二部分：IDEA 环境配置步骤

为了让 IDEA 能同时运行 Java 和 Vue，请按以下步骤操作：

#### 1. 安装必要插件 (Plugins)

IDEA Ultimate 通常自带，如果是 Community 版，请安装：

- **Vue.js**：提供 `.vue` 文件的语法高亮和代码提示。
- **NodeJS**：允许 IDEA 运行 npm 命令。
- **Lombok** (后端用)：消除 Java 的 getter/setter 冗余代码。

#### 2. 导入项目到 IDEA

1. 打开 IDEA，选择 `Open`，选择 `DaigouSystem` 文件夹。
2. IDEA 会自动识别 `server` 文件夹为 Maven 项目，`client` 文件夹为 Node.js 项目。
3. 如果没有自动识别，点击右侧的 **Maven** 面板，点击刷新按钮（Reload All Maven Projects）。

### 🚀 第三部分：运行方式（保姆级教程）

在 IDEA 中，你不需要打开两个窗口，利用 **"运行配置 (Run Configurations)"** 即可同时启动前后端。

#### 步骤 1：配置前端运行 (npm run serve)

1. 点击 IDEA 右上角的 `Add Configuration` (或者在 `Run` -> `Edit Configurations`)。
2. 点击 `+` 号，选择 **`npm`**。
3. 配置参数如下：
    - **Name**: `Client Dev` (名字随便起)
    - **Package.json**: 点击右边的文件夹图标，选择 `client/package.json` 文件。
    - **Command**: `run`
    - **Scripts**: `dev` (或者 `serve`，取决于你的 Vue 脚本配置)。
4. 点击 `OK` 保存。

#### 步骤 2：配置后端运行 (SpringBoot)

1. 再次点击 `Add Configuration`。
2. 点击 `+` 号，选择 **`Application`**。
3. 配置参数如下：
    - **Name**: `Server Boot`
    - **Main Class**: 找到你的 `DaigouApplication.java` 文件（通常 IDEA 会自动填好）。
    - **Use classpath of module**: 选择 `server` 模块。
4. 点击 `OK` 保存。

#### 步骤 3：一键启动（并行运行）

IDEA 默认是串行的，我们需要手动操作一下来实现“双开”：

1. **先启动后端**：在右上角下拉框选择 `Server Boot`，点击绿色三角形 ▶️。等待控制台出现 `Started DaigouApplication in 5.5 seconds` (Tomcat started on port(s): 8080)。
2. **再启动前端**：在右上角下拉框选择 `Client Dev`，点击绿色三角形 ▶️。
    - _注意：第一次运行前端可能会卡在 `Installing dependencies`，这是在下载 node_modules，耐心等待几分钟即可。_

**运行结果：**

- 后端控制台：显示 SpringBoot 启动日志。
- 前端控制台：显示 `Local: http://localhost:8080` (通常前端是 8080，后端改为了 9090，或者前端是 5173，后端是 8080)。

### 💡 常见问题与解决方案（针对 IDEA 全栈开发）

#### 1. 跨域问题 (CORS)

当你在 IDEA 的浏览器里打开前端页面，前端向后端发送请求时，可能会报错 `Access-Control-Allow-Origin`。

- **解决方法**：在后端的启动类 `DaigouApplication.java` 或者全局配置类上，加上注解：
    
    java
    
    ```
    @CrossOrigin(origins = "*") // 允许所有来源，仅用于开发环境
    ```
    
    或者在 `application.yml` 中配置代理（进阶）。

#### 2. 端口冲突

如果前端和后端都默认用了 8080 端口。

- **修改后端端口**：打开 `server/src/main/resources/application.yml`，添加：
    
    yaml
    
    ```
    server:
      port: 9090 # 后端改为9090
    ```
    
- **修改前端端口**：打开 `client/vite.config.js` (或 vue.config.js)，添加：
    
    js
    
    ```
    export default defineConfig({
      server: {
        port: 8080, // 前端保持8080
        proxy: {
          '/api': {
            target: 'http://localhost:9090', // 代理到后端
            changeOrigin: true
          }
        }
      }
    })
    ```
    

#### 3. 数据库连接

- IDEA 右侧有一个 **Database** 标签。
- 点击 `+` -> `Data Source` -> `MySQL`。
- 填写你的数据库地址、用户名和密码。这样你不需要 Navicat，直接在 IDEA 里就能执行 SQL 查看数据。

### 📝 总结

使用 IDEA 全栈开发，你的桌面只需要：

1. **一个 IDEA 窗口**：负责写代码、跑后端、跑前端。
2. **一个浏览器窗口**：负责看效果。

这种方式最简洁，也最符合你“完全使用 IDEA”的需求。祝你毕设顺利！