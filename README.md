# 校园生活 Agent「雄鹰项目」

面向校园场景的一站式生活服务应用，集成 AI 校园问答、失物招领、心理咨询、快递代取、校园兼职、报修维护、请假、日程提醒、志愿服务、天气查询等 18 个功能模块。

> **项目状态：参赛作品。** 已完成全部模块开发与前后端联调，输出可运行原型，尚未对外上线运营。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 2.6 + vue-router 3 + axios，Bootstrap 5 |
| 后端 | Spring Boot + MyBatis-Plus + MySQL |
| AI 能力 | 阿里云 DashScope（通义千问 qwen），支持文本对话与图片理解 |
| 第三方接口 | 高德地图 Web 服务 API（天气查询） |
| 构建 | Maven（后端）／ Vue CLI 4（前端） |

## 功能模块（18 个）

| 模块 | 说明 |
|---|---|
| Chat / ImageChat | AI 校园问答，支持文字对话与图片理解 |
| LostFound | 失物招领发布与查询 |
| EmotionRecord | 情绪记录（心理咨询方向） |
| Express | 快递代取 |
| PartTime | 校园兼职信息 |
| Repair / Maintenance | 设施报修与维护管理 |
| Leave | 请假申请 |
| Schedule / Remind | 日程管理与事项提醒 |
| Volunteer | 志愿服务活动 |
| Weather | 天气查询 |
| CampusKnowledge | 校园知识库 |
| Info / Feedback | 校园信息发布与意见反馈 |
| User / Admin | 用户中心与后台管理 |

## 我在项目中负责

- **需求拆解与模块设计**：规划 18 个功能模块，制定里程碑计划，协调 3 人团队按期交付
- **前端开发**：核心功能页面开发、路由配置、与后端接口对接
- **后端开发**：业务接口实现（Spring Boot + MyBatis-Plus）、前后端联调
- **工作流建设**：引入 AI 辅助编码工具，承担代码评审与迭代改进

## 目录结构

```
campus-agent/                后端服务（Spring Boot）
  src/main/java/com/campus/agent/
    controller/              18 个业务模块的接口层
    service/                 业务逻辑层
    mapper/ entity/          数据访问层与实体
  src/main/resources/
    application.yml          配置（已脱敏）
campus-agent-frontend/
  campus-agent-vue/          前端工程（Vue 2）
SQL/                         数据库建表脚本
```

## 本地启动

**1. 数据库**

创建 MySQL 数据库 `campus_agent`，执行 `SQL/` 目录下的建表脚本。

**2. 后端**

编辑 `campus-agent/src/main/resources/application.yml`，填入你自己的：

- MySQL 用户名与密码
- 阿里云 DashScope API Key（`qwen.api-key`）
- 高德 Web 服务 API Key（`weather.api-key`）

```bash
cd campus-agent
mvn spring-boot:run
```

服务默认运行在 `http://localhost:8080`。

**3. 前端**

```bash
cd campus-agent-frontend/campus-agent-vue
npm install
npm run serve
```

> 说明：`application.yml` 中的数据库密码与 API Key 已做占位符脱敏处理，本地运行需自行填写，仓库中不包含任何真实凭证。
