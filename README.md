# AI 知识教研室

一个基于 Spring Boot + Vue 3 的在线课堂和智能教研室系统，支持实时聊天、协同文档编辑、AI 对话和知识库管理。

## 功能特性

### 实时课堂模块
- 实时聊天室 (基于 Netty WebSocket)
- 在线成员管理
- 协同文档编辑
- 文件上传/下载
- 课堂状态管理

### 智能教研室模块
- AI 智能对话助手
- 知识库管理 (上传/分类/搜索)
- RAG 检索增强生成
- 对话历史记录 (按月份归档)
- 多模型支持 (智谱AI/通义千问/OpenAI)

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.1
- Spring Data Redis
- MyBatis-Plus 3.5.5
- Netty 4.1.104 (WebSocket)
- Spring AI (LLM 集成)
- MySQL 8.0
- Redis

### 前端
- Vue 3.4
- TypeScript
- Vite 5.0
- Element Plus
- Pinia (状态管理)
- Socket.IO Client
- Quill (富文本编辑器)

## 项目结构

```
ai-knowledge-chat/
├── backend/                    # 后端项目
│   ├── src/main/java/com/aikc/
│   │   ├── controller/         # REST 控制器
│   │   ├── service/            # 业务逻辑
│   │   ├── mapper/             # 数据访问
│   │   ├── entity/             # 数据库实体
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── websocket/          # WebSocket 服务
│   │   ├── security/           # 安全认证
│   │   └── common/             # 公共类
│   └── pom.xml
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── classroom/      # 课堂模块
│   │   │   ├── research/       # 教研室模块
│   │   │   └── knowledge/      # 知识库管理
│   │   ├── components/         # 公共组件
│   │   ├── api/                # API 接口
│   │   ├── stores/             # 状态管理
│   │   ├── router/             # 路由配置
│   │   └── utils/              # 工具函数
│   └── package.json
│
└── docs/                       # 项目文档
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0
- Redis 6.0+

### 数据库初始化

```sql
CREATE DATABASE ai_knowledge_chat DEFAULT CHARSET utf8mb4;
```

执行 `docs/schema.sql` 初始化表结构。

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 配置说明

### 后端配置 (`application.yml`)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_knowledge_chat
    username: root
    password: your-password

  # Redis 配置
  data:
    redis:
      host: localhost
      port: 6379

  # AI 配置
  ai:
    openai:
      api-key: your-api-key
      base-url: https://open.bigmodel.cn/api/paas/v4
```

### 环境变量

创建 `.env` 文件：

```env
AI_API_KEY=your-api-key
AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4
AI_MODEL=glm-4
```

## 数据库设计

### 核心表

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| classroom | 课堂表 |
| classroom_member | 课堂成员表 |
| chat_message | 聊天消息表 |
| doc | 协同文档表 |
| knowledge_base | 知识库表 |
| knowledge_doc | 知识文档表 |
| conversation | 对话会话表 |
| conversation_message | 对话消息表 |

详细设计见 `docs/database.md`

## API 文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 课堂接口
- `GET /api/classroom` - 获取课堂列表
- `POST /api/classroom` - 创建课堂
- `GET /api/classroom/{id}` - 获取课堂详情
- `POST /api/classroom/{id}/join` - 加入课堂

### 教研室接口
- `GET /api/conversation` - 获取对话列表
- `POST /api/conversation` - 创建对话
- `POST /api/conversation/{id}/chat` - 发送消息

### 知识库接口
- `GET /api/knowledge` - 获取知识库列表
- `POST /api/knowledge` - 创建知识库
- `POST /api/knowledge/{id}/upload` - 上传文档

详细 API 文档见 `docs/api.md`

## WebSocket 协议

### 连接
```
ws://localhost:9090/ws?token=xxx&classroomId=xxx
```

### 消息格式

```json
{
  "type": "CHAT",
  "classroomId": "123",
  "userId": "456",
  "content": "hello"
}
```

详细协议见 `docs/websocket.md`

## 开发计划

- [x] 项目脚手架搭建
- [ ] 用户认证系统 (JWT)
- [ ] 实时聊天功能
- [ ] 协同文档编辑
- [ ] AI 对话集成
- [ ] 知识库管理
- [ ] 向量存储与检索
- [ ] 对话历史归档

## License

MIT
