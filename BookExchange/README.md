# 校园二手书交易平台 (Campus Second-Hand Book Exchange Platform)

一个功能完善、界面美观的Android校园二手书交易应用，采用现代化的MVVM架构和Material Design设计语言。

## ✨ 核心功能

### 1. 用户系统
- ✅ **用户注册与登录**
  - 邮箱注册，密码加密存储（SHA-256）
  - 完善的输入验证（邮箱格式、密码长度、用户名唯一性）
  - 持久化登录状态
  
- ✅ **学校邮箱验证**
  - 六位数字验证码系统
  - 防重复发送机制（60秒倒计时）
  - 频率限制（1小时最多3次）
  - 验证码有效期管理（10分钟）
  - 完善的错误处理和用户提示

- 🔜 **用户信用系统**
  - 初始信用分100分
  - 交易完成度影响信用分
  - 用户举报机制
  - 账号封禁功能

### 2. 书籍管理
- ✅ **书籍数据模型**
  - 书名、作者、ISBN、描述
  - 价格、成色（全新、九成新、八成新、七成新）
  - 分类（教材、小说、技术书籍、其他）
  - 多图片支持
  - 标签系统
  - 浏览量和收藏量统计

- 🔜 **书籍发布与编辑**
  - 多图片上传
  - 详细信息填写
  - 图片压缩和安全检查

- 🔜 **搜索与筛选**
  - 关键词搜索（书名、作者、描述）
  - 分类筛选
  - 价格区间筛选
  - 成色筛选
  - 排序（最新、价格、热度）

- 🔜 **收藏功能**
  - 一键收藏/取消收藏
  - 收藏列表管理
  - 收藏书籍状态通知

### 3. 交易系统
- ✅ **交易状态机**
  - requested（买家发起请求）
  - accepted（卖家接受）
  - rejected（卖家拒绝）
  - completed（交易完成）
  - cancelled（交易取消）

- 🔜 **交易流程**
  - 买家发起购买请求
  - 卖家查看并响应请求
  - 双方确认交易完成
  - 交易历史记录

- 🔜 **消息系统**
  - 买卖双方实时沟通
  - 未读消息提醒
  - 消息历史记录
  - 书籍相关消息关联

### 4. UI/UX优化
- ✅ **Material Design**
  - 现代化的配色方案
  - 统一的组件样式
  - 平滑的动画过渡
  - 卡片式布局

- ✅ **加载状态管理**
  - 按钮禁用防重复点击
  - ProgressBar加载指示
  - 倒计时显示

- 🔜 **更多优化**
  - 骨架屏加载效果
  - 空状态页面设计
  - 错误状态处理
  - 下拉刷新
  - 图片懒加载

### 5. 安全性
- ✅ **密码安全**
  - SHA-256加密
  - 不可逆存储

- ✅ **输入验证**
  - 客户端验证
  - SQL注入防护（Room自动处理）
  - XSS防护

- 🔜 **更多安全措施**
  - 图片内容安全检查
  - 敏感词过滤
  - 用户行为审计日志

## 🏗️ 技术架构

### 架构模式
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** （数据层抽象）
- **LiveData** （响应式数据）
- **Coroutines** （异步处理）

### 主要技术栈
- **语言**: Kotlin
- **数据库**: Room (SQLite)
- **UI**: Material Components
- **异步**: Kotlin Coroutines
- **图片**: Glide
- **导航**: Navigation Component

### 项目结构
```
com.example.bookexchange/
├── model/              # 数据模型
│   ├── User.kt
│   ├── Book.kt
│   ├── Transaction.kt
│   ├── Favorite.kt
│   ├── VerificationCode.kt
│   └── Message.kt
├── database/           # 数据库层
│   ├── AppDatabase.kt
│   ├── UserDao.kt
│   ├── BookDao.kt
│   └── ...
├── repository/         # 数据仓库层
│   ├── AuthRepository.kt
│   └── BookRepository.kt
├── viewmodel/          # ViewModel层
│   └── AuthViewModel.kt
├── ui/                 # UI层
│   ├── MainActivity.kt
│   ├── LoginActivity.kt
│   ├── RegisterActivity.kt
│   └── fragments/
└── util/               # 工具类
    └── AppExecutors.kt
```

## 📱 界面截图

（待添加实际运行截图）

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox or later
- Kotlin 1.9.0+
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

### 构建项目
```bash
# 克隆仓库
git clone [repository-url]

# 打开项目
在Android Studio中打开BookExchange目录

# 构建并运行
点击运行按钮或使用 Shift+F10
```

## 📝 代码优化亮点

### 1. 防重复操作
```kotlin
// 发送验证码时检查是否正在发送
if (_sendingCode.value == true) {
    _toastMessage.value = "正在发送，请稍候"
    return
}
```

### 2. 倒计时功能
```kotlin
private fun startCountdown(seconds: Int) {
    _resendCountdown.value = seconds
    viewModelScope.launch {
        repeat(seconds) {
            kotlinx.coroutines.delay(1000)
            _resendCountdown.value = (_resendCountdown.value ?: 0) - 1
        }
    }
}
```

### 3. 频率限制
```kotlin
// 数据库层面的频率检查
val oneHourAgo = System.currentTimeMillis() - 3600000
val codesSent = verificationCodeDao.getCodesSentCount(schoolEmail, oneHourAgo)
if (codesSent >= MAX_CODES_PER_HOUR) {
    callback.onError(ERROR_RATE_LIMIT, "发送次数过多，请1小时后再试")
    return
}
```

### 4. 统一错误处理
```kotlin
private fun getErrorMessage(errorCode: Int, defaultMessage: String): String {
    return when (errorCode) {
        AuthRepository.ERROR_RATE_LIMIT -> "操作过于频繁，请稍后再试"
        AuthRepository.ERROR_INVALID_CODE -> "验证码错误"
        // ... 更多错误码映射
        else -> defaultMessage
    }
}
```

## 🎯 下一步计划

- [ ] 完善书籍列表和详情页面
- [ ] 实现图片上传和管理
- [ ] 添加实时消息功能
- [ ] 实现交易流程完整闭环
- [ ] 添加用户评价系统
- [ ] 实现推送通知
- [ ] 添加数据统计和分析
- [ ] 编写单元测试和UI测试
- [ ] 性能优化和内存泄漏检测
- [ ] 国际化支持

## 📄 License

MIT License - 可自由使用和修改

## 👥 贡献

欢迎提交Issue和Pull Request！

---

**开发者**: Campus Development Team  
**版本**: 1.0.0  
**更新时间**: 2026-01-19
