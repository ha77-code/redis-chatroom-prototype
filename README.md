# Redis Chatroom Prototype

## 项目简介
这是一个基于 `Spring Boot + Redis` 的网络聊天室原型系统，采用本地 Web 服务加浏览器访问的运行方式。

项目包含两类角色：
- 普通用户：注册、登录、修改资料、加入聊天室、发送消息、查看历史消息和在线成员
- 管理员：在普通用户能力基础上，额外支持禁言、踢出用户、删除消息

默认技术路线：
- 后端：Spring Boot 3
- 缓存与会话：Redis
- 消息接收：HTTP 轮询
- 前端：内置静态页面，启动后访问 `http://localhost:8080/`

## 环境要求
- JDK 17
- Maven 3.9+ 或 Maven Wrapper

## 直接运行 JAR
1. 打包：

```bash
mvn clean package -DskipTests
```

2. 启动：

```bash
java -jar target/redis-chatroom-prototype-1.0-SNAPSHOT.jar
```

3. 访问：

浏览器打开 [http://localhost:8080/](http://localhost:8080/)

说明：
- 应用内置嵌入式 Redis；如果本机 `6379` 已有 Redis，会优先复用现有实例
- 启动成功后，程序会自动尝试打开默认浏览器
- 如不希望自动打开浏览器，可追加参数：

```bash
java -jar target/redis-chatroom-prototype-1.0-SNAPSHOT.jar --chatroom.launch.open-browser=false
```

## 打包 Windows EXE 启动器
这个项目现在支持打包成 Windows 可执行启动器。打包结果自带运行时，所以目标电脑不需要安装 Java，也不需要安装 Maven。

### 前提
- Windows
- JDK 17
- 已设置 `JAVA_HOME`
- `jpackage` 可用（JDK 17 自带）

### 步骤
1. 先构建 Jar：

```powershell
mvn clean package -DskipTests
```

2. 再执行打包脚本：

```powershell
.\scripts\build-windows-exe.ps1
```

### 产物
- 启动器目录：`dist\RedisChatroom`
- 可执行文件：`dist\RedisChatroom\RedisChatroom.exe`
- 可分发压缩包：`dist\RedisChatroom-portable.zip`

### 行为说明
- 双击 `RedisChatroom.exe` 后会启动后端服务
- 服务就绪后会自动打开默认浏览器访问首页
- 打包结果自带运行时，不要求目标机器预装 Java
- 当前默认保留控制台窗口，便于查看启动日志和排错

## 默认账号
- 管理员：`admin / admin123`

## 停止方式
- 直接关闭控制台窗口，或在控制台按 `Ctrl + C`

## 设计文档
详细设计见同目录文件：
- [基于Redis的网络聊天室原型系统设计文档.md](./基于Redis的网络聊天室原型系统设计文档.md)
