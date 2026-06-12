# 基于 Redis 的网络聊天室原型系统设计文档

## 1. 项目概述
### 1.1 项目名称
基于 Redis 中间件/数据库技术的网络聊天室原型系统

### 1.2 项目目标
设计并实现一个面向普通用户和管理员的网络聊天室原型系统。系统支持用户注册、登录、加入聊天室、发送与接收消息、查看在线用户、查看历史聊天记录等功能；管理员在此基础上支持禁言用户、踢出用户、删除不当消息等管理操作。

系统采用 Java Web 技术实现，Redis 用于保存在线状态、会话、聊天记录缓存及用户管控信息。

### 1.3 设计原则
1. 结构简单，适合作为课程原型系统实现。
2. 模块清晰，便于在 IDEA 中分层开发。
3. Redis 的使用必须体现其在聊天室中的实时状态管理作用。
4. 先保证功能可演示，再考虑后续扩展能力。

## 2. 功能需求
### 2.1 普通用户功能
1. 用户注册
2. 用户登录
3. 修改个人信息
4. 加入聊天室
5. 退出聊天室
6. 发送消息
7. 接收消息
8. 查看在线用户列表
9. 查看聊天记录

### 2.2 管理员功能
1. 管理聊天室
2. 禁言用户
3. 踢出用户
4. 删除聊天记录

### 2.3 非功能需求
1. 系统应支持多用户并发访问的原型演示。
2. 消息收发响应应尽量快速。
3. 用户会话应有失效控制。
4. 聊天记录和在线状态应通过 Redis 进行统一管理。
5. 系统应具备基本的权限校验和异常处理能力。

## 3. 总体架构设计
系统采用典型分层架构：

```text
+--------------------------------------------------+
| Presentation Layer                               |
| Controller: AuthController / UserController      |
|            ChatRoomController / AdminController  |
+--------------------------------------------------+
| Business Layer                                   |
| AuthService / UserService / ChatService /        |
| AdminService                                     |
+--------------------------------------------------+
| Data Access Layer                                |
| SessionRepository / OnlineUserRepository /       |
| MessageRepository / ModerationRepository         |
+--------------------------------------------------+
| Redis Layer                                      |
| Session / Online Users / Message List / Mute /   |
| Kick / Room Meta                                 |
+--------------------------------------------------+
```

### 3.1 各层职责
1. `Controller`：接收请求，返回响应，完成参数校验。
2. `Service`：封装业务逻辑，处理登录、发消息、禁言等核心流程。
3. `Repository`：封装对 Redis 的访问细节。
4. `Redis`：作为中间件与运行时数据存储层，保存在线状态、会话、消息与管理信息。

## 4. Redis 数据设计
### 4.1 Session 会话
- Key：`session:{token}`
- 类型：Hash
- Value：`userId`、`username`、`role`、`loginTime`、`expireAt`

作用：保存当前登录用户的会话信息，用于接口认证。

### 4.2 在线用户
- Key：`online:users`
- 类型：Set
- Value：在线用户 ID 集合

- Key：`user:status:{userId}`
- 类型：Hash
- Value：`roomId`、`online`、`lastActiveTime`

作用：保存当前在线用户及其所在聊天室状态。

### 4.3 聊天记录
- Key：`room:{roomId}:messages`
- 类型：List
- Value：消息 JSON 字符串

消息结构建议：

```json
{
  "messageId": "msg_1001",
  "roomId": "room_1",
  "senderId": "u001",
  "senderName": "Tom",
  "content": "hello",
  "sendTime": "2026-06-12 14:30:00",
  "deleted": false
}
```

作用：保存聊天室消息，用于轮询拉取和历史记录查询。

### 4.4 禁言信息
- Key：`mute:{roomId}:{userId}`
- 类型：String
- Value：禁言截止时间戳

作用：控制用户是否允许发送消息。

### 4.5 踢出信息
- Key：`kick:{roomId}:{userId}`
- 类型：String
- Value：状态标记或失效时间

作用：限制违规用户继续留在聊天室。

### 4.6 聊天室元信息
- Key：`room:{roomId}:meta`
- 类型：Hash
- Value：`roomName`、`status`、`notice`、`createTime`

作用：保存聊天室基本配置。

## 5. 核心类设计
### 5.1 实体类
```text
User
- userId
- username
- password
- nickname
- role
- status
- createTime

Admin extends User

ChatRoom
- roomId
- roomName
- status
- notice

Message
- messageId
- roomId
- senderId
- senderName
- content
- sendTime
- deleted

SessionInfo
- token
- userId
- username
- role
- loginTime
- expireAt

MuteRecord
- roomId
- userId
- expireAt

OperationLog
- logId
- adminId
- operationType
- targetUserId
- targetMessageId
- operationTime
- description
```

### 5.2 控制层
1. `AuthController`
   - `register()`
   - `login()`

2. `UserController`
   - `updateProfile()`

3. `ChatRoomController`
   - `joinRoom()`
   - `exitRoom()`
   - `sendMessage()`
   - `pollMessages()`
   - `getOnlineUsers()`
   - `getHistoryMessages()`

4. `AdminController`
   - `muteUser()`
   - `kickUser()`
   - `deleteMessage()`

### 5.3 服务层
1. `AuthService`
   - 注册用户
   - 登录校验
   - 生成 token
   - 创建 session

2. `UserService`
   - 修改用户资料
   - 查询用户信息

3. `ChatService`
   - 加入/退出聊天室
   - 校验用户状态
   - 发送消息
   - 拉取新消息
   - 查询在线用户
   - 查询历史消息

4. `AdminService`
   - 禁言用户
   - 踢出用户
   - 删除消息
   - 记录管理操作

### 5.4 数据访问层
1. `SessionRepository`
2. `OnlineUserRepository`
3. `MessageRepository`
4. `ModerationRepository`

## 6. 接口设计
### 6.1 认证接口
- `POST /auth/register`
- `POST /auth/login`

### 6.2 用户接口
- `POST /user/profile/update`

### 6.3 聊天室接口
- `POST /rooms/join`
- `POST /rooms/exit`
- `POST /messages/send`
- `GET /messages/poll?roomId=xxx&lastMessageId=xxx`
- `GET /rooms/online-users?roomId=xxx`
- `GET /rooms/history?roomId=xxx`

### 6.4 管理接口
- `POST /admin/mute`
- `POST /admin/kick`
- `POST /admin/messages/delete`

## 7. 核心业务流程
### 7.1 用户发送消息流程
1. 用户登录获取 token。
2. 用户加入聊天室。
3. 客户端发起 `POST /messages/send` 请求。
4. 服务端校验 session 是否有效。
5. 服务端校验用户是否在聊天室中。
6. 服务端校验用户是否被禁言。
7. 校验通过后将消息写入 `room:{roomId}:messages`。
8. 返回发送成功结果。
9. 其他用户通过轮询接口拉取最新消息。

### 7.2 管理员禁言流程
1. 管理员登录系统。
2. 管理员选择目标用户并设置禁言时长。
3. 系统写入 `mute:{roomId}:{userId}`。
4. 用户再次发送消息时，系统检测禁言状态并拒绝发送。
5. 返回“用户已被禁言”的提示。

### 7.3 删除消息流程
1. 管理员提交消息 ID。
2. 系统在消息列表中定位该消息。
3. 将消息标记为已删除或从查询结果中过滤。
4. 后续历史查询不再展示该消息内容。

## 8. 关键设计说明
### 8.1 为什么使用 HTTP 轮询
本系统定位为课程原型，实现难度需要可控。相比 WebSocket，HTTP 轮询更容易在 Java Web 基础上快速完成，同时仍能展示聊天室的消息收发逻辑。Redis 在该方案中主要负责消息存储、在线状态维护与用户管控，而不强依赖实时推送框架。

### 8.2 为什么 Redis 不单独作为业务对象建模
Redis 是实现技术，不是系统参与者。

在设计中，Redis 主要承担：
1. 保存用户 session
2. 保存在线用户状态
3. 保存聊天记录
4. 保存禁言和踢出状态
5. 缓存聊天室元信息

### 8.3 权限控制
1. 普通用户只能访问自己的用户功能和聊天室功能。
2. 管理员拥有普通用户全部能力。
3. 管理员额外拥有禁言、踢出、删消息权限。

## 9. 测试方案
### 9.1 功能测试
1. 用户注册与登录是否成功。
2. 用户加入聊天室后是否可见在线列表。
3. 用户发送消息后，其他用户是否能通过轮询收到。
4. 历史消息是否可查询。
5. 用户退出聊天室后是否从在线列表移除。
6. 管理员禁言后，目标用户是否无法发言。
7. 管理员踢出后，目标用户是否无法继续聊天。
8. 管理员删除消息后，历史记录中是否不再显示该消息。

### 9.2 异常测试
1. 未登录用户访问聊天室接口。
2. token 失效后继续请求。
3. 非管理员调用管理接口。
4. 被禁言用户发送消息。
5. 被踢用户轮询消息或重新发言。

## 10. 结论
本系统采用 Java Web + Redis 的实现方案，结构清晰，功能完整，能够满足课程原型系统对于聊天室场景的基本要求。

Redis 在本系统中不仅承担缓存作用，还负责维护在线状态、消息记录、会话信息和管理控制数据，体现了其中间件/数据库技术在实时聊天系统中的应用价值。
