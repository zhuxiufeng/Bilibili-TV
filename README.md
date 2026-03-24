# Bilibili-TV 📺

一个基于 **Kotlin + Jetpack Compose** 构建的原生 Bilibili 电视端第三方客户端 (Android TV)。

## ✨ 主要功能 (Features)
- 🏠 **首页浏览 (Home)**：大屏友好的视频推荐与分区内容。
- 📺 **视频播放 (Player)**：针对遥控器优化的视频播放体验。
- 👥 **关注动态 (Following)**：查看已关注 UP 主的最新视频更新。
- 🔑 **扫码登录 (Login)**：原生扫码登录 B站账号（基于 `QrCodeUtil`）。

## 🛠️ 技术栈 (Tech Stack)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (专为大屏与电视重构布局)
- **Architecture**: MVVM (带由 ViewModel 驱动的状态管理)
- **Build Tool**: Gradle Kotlin DSL (`build.gradle.kts`)

## 🚀 运行方法 (How to Run)
1. **克隆项目到本地**:
   ```bash
   git clone https://github.com/zhuxiufeng/Bilibili-TV.git
   ```
2. **导入项目**:
   使用最新版 **Android Studio** (推荐 Iguana 或更高版本) 打开本工程的根目录。
3. **同步 Gradle**:
   等待 Gradle 构建及同步完成。
4. **编译与运行**:
   通过 USB 调试连接您的智能电视、机顶盒或运行 Android TV 模拟器，点击运行。

## 🤝 贡献说明 (Contributing)
欢迎提交 PR 与 Issue。如果您对现有 UI/UX 或播放器底层有更好的实现思路，非常期待您的加入。

## 📄 许可证 (License)
本项目仅供学习与交流使用，所有的 Bilibili API 接口数据与内容归属于 Bilibili 官方所有。
