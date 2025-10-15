# TRplayer - Emby播放器 Android应用

一个现代化的Android Emby媒体播放器应用，采用最新的Android开发技术和架构模式。

## 🚀 功能特性

### 📱 核心功能
- **Emby服务器连接** - 支持多服务器配置和管理
- **媒体库浏览** - 浏览电影、电视剧、音乐等媒体内容
- **视频播放** - 支持高清视频播放，提供播放控制功能
- **音频播放** - 支持音乐播放，包含播放列表管理
- **响应式UI** - 基于Jetpack Compose的现代化用户界面

### 🏗️ 技术架构
- **MVVM架构** - 采用Model-View-ViewModel设计模式
- **依赖注入** - 使用Hilt进行依赖注入管理
- **响应式编程** - 基于Kotlin Coroutines和Flow
- **导航系统** - 使用Jetpack Navigation进行页面导航
- **模块化设计** - 清晰的数据层、领域层、表示层分离

## 📸 应用截图

*应用界面截图将在后续版本中添加*

## 🛠️ 技术栈

### 核心框架
- **Kotlin** - 主要编程语言
- **Jetpack Compose** - 声明式UI框架
- **Hilt** - 依赖注入框架
- **Navigation** - 页面导航组件
- **ViewModel** - 生命周期感知的数据管理

### 网络与数据
- **Retrofit** - HTTP客户端
- **OkHttp** - HTTP网络库
- **Room** - 本地数据库（计划中）

### 媒体播放
- **ExoPlayer** - 媒体播放引擎（计划集成）

## 📁 项目结构

```
app/src/main/java/com/trplayer/embyplayer/
├── data/           # 数据层
│   ├── local/      # 本地数据源
│   ├── remote/     # 远程数据源
│   └── repository/ # 数据仓库
├── domain/         # 领域层
│   ├── model/      # 领域模型
│   └── repository/ # 领域接口
├── presentation/   # 表示层
│   ├── screens/    # 界面屏幕
│   ├── components/ # UI组件
│   ├── navigation/ # 导航配置
│   ├── theme/      # 主题配置
│   └── viewmodels/ # ViewModel
└── di/             # 依赖注入
    └── NetworkModule.kt
```

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 31+
- Java 11+

### 构建步骤

1. **克隆项目**
```bash
git clone https://github.com/tgszy/TRplayer.git
cd TRplayer
```

2. **打开项目**
- 使用Android Studio打开项目根目录

3. **同步项目**
- Android Studio会自动同步Gradle依赖

4. **构建运行**
- 连接Android设备或启动模拟器
- 点击"Run"按钮构建并运行应用

## ⚙️ 配置说明

### Emby服务器配置
应用支持配置多个Emby服务器：
- 服务器地址
- 端口号
- 用户名和密码（或API密钥）

### 播放设置
- 视频质量选择
- 字幕设置
- 播放器配置

## 🔄 开发计划

### v1.0.1 版本功能
- ✅ 基础项目架构搭建
- ✅ Emby API服务集成
- ✅ 服务器管理界面
- ✅ 基础导航系统
- ⏳ 媒体库浏览界面
- ⏳ 视频播放功能
- ⏳ 音频播放功能

### 未来版本规划
- 离线下载功能
- 收藏和播放列表
- 多语言支持
- 深色主题
- 小部件支持

## 🤝 贡献指南

我们欢迎社区贡献！请遵循以下步骤：

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目主页: [https://github.com/tgszy/TRplayer](https://github.com/tgszy/TRplayer)
- 问题反馈: [GitHub Issues](https://github.com/tgszy/TRplayer/issues)

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

⭐ 如果这个项目对您有帮助，请给我们一个Star！