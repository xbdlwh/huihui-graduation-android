# 汇火应用 (Huihu App) - 功能模块总结

## 项目概述

**汇火应用** 是一个基于 Jetpack Compose 开发的 Android 美食推荐和社交应用。

### 技术架构
- **UI 框架**: Jetpack Compose + Material 3
- **架构模式**: MVVM (Model-View-ViewModel)
- **依赖注入**: 手动依赖注入 (Container 模式)
- **网络请求**: Retrofit + Kotlinx Serialization
- **本地存储**: Room 数据库 + DataStore Preferences
- **图片加载**: Coil 3
- **分页加载**: AndroidX Paging 3
- **导航**: Navigation3 (Compose 原生导航)

---

## 功能模块列表

### 1. 认证模块 (Authentication)
- **登录页面**: 用户名/密码登录
- **注册页面**: 邮箱、用户名、密码注册
- **API**: 登录、注册、获取当前用户信息、更新用户信息

### 2. 首页模块 (Home)
- 三标签底部导航结构
  - 论坛标签: 显示话题列表
  - 美食标签: 显示今日吃什么推荐
  - 我的标签: 显示个人中心
- 可切换的随机推荐模式
- 下拉刷新
- 发布话题入口 (FAB)

### 3. 论坛/话题模块 (Forum/Topic)
- **话题列表页**: 分页加载话题列表
- **话题详情页**: 查看话题详情和评论
- **发布话题页**: 发布话题或评论
- **话题管理页**: 管理我发布的话题
- **功能**: 点赞/取消点赞、图片上传和预览、话题删除

### 4. 美食推荐模块 (Food Recommendation)
- 展示推荐菜品
- 用户反馈操作: 就这样/换一换/不喜欢
- 随机模式开关
- 后台预加载推荐数据
- 本地缓存推荐菜品 (Room)

### 5. 新人引导模块 (Onboarding)
- 建立用户口味画像
- 2轮菜品选择流程
- 右滑喜欢/左滑跳过
- 基于选择构建用户口味画像

### 6. 个人中心模块 (Mine)
- 用户信息卡片
- 统计信息: 决策成功/失败次数、成功率、口味偏好
- 功能入口:
  - 编辑资料
  - 决策成功 (喜欢的菜品)
  - 我的话题管理
  - 建议列表
  - 食物轨迹
  - 退出登录

### 7. 编辑资料模块 (Edit Profile)
- 头像上传和预览
- 用户名修改
- 保存更改

### 8. 决策成功/喜欢菜品模块 (Food Liked)
- 展示用户历史点赞的菜品列表
- 显示菜品详情 (图片、名称、餐厅、位置、描述)

### 9. 建议模块 (Suggestion)
- **建议列表页**: 查看我提交的建议
- **建议详情页**: 查看建议详情和审核意见
- **添加建议页**: 创建新建议
- **建议类型**:
  - 新增菜品 (ADD_FOOD)
  - 更新菜品 (UPDATE_FOOD)
  - 其他建议 (OTHER)
- **建议状态**: 待处理、已通过、已拒绝、准备中、处理中、已完成

### 10. 食物轨迹模块 (Food Track)
- 使用 WebView 嵌入 H5 页面
- 展示用户饮食记录和轨迹可视化

### 11. 图片预览模块 (Image Preview)
- 全屏图片预览
- 支持多图横向滑动
- 底部页码指示器

### 12. 启动页 (Splash)
- 应用启动时显示
- 检查认证状态并路由

---

## 数据层架构

### Repository 层
- `AuthRepository`: 认证相关
- `FoodRepository`: 食物推荐和缓存
- `TopicRepository`: 话题管理
- `SuggestionRepository`: 建议管理
- `RestaurantRepository`: 餐厅数据
- `LocalStoreRepository`: 本地存储 (DataStore)

### Source 层 (Retrofit API)
- `AuthSource`: 认证 API
- `FoodSource`: 食物 API
- `TopicSource`: 话题 API
- `SuggestionSource`: 建议 API
- `RestaurantSource`: 餐厅 API

### 本地数据层
- **Room 数据库**: `AppDatabase` (food_cache 表)
- **DAO**: `FoodCacheDao`
- **Entity**: `FoodCacheEntity`
- **DataStore**: 存储 Token、设置等

---

## 功能模块汇总表

| 模块 | 页面 | ViewModel | 主要功能 |
|------|------|-----------|----------|
| 认证 | Login, Register | AuthViewModel | 登录/注册 |
| 首页 | Home | HomeViewModel | 三标签导航 |
| 论坛 | Forum, TopicDetail, CreateTopic, TopicManage | ForumViewModel, TopicDetailViewModel, CreateTopicViewModel, TopicManageViewModel | 话题浏览/发布/评论/管理 |
| 美食推荐 | FoodRecommendation | FoodRecommendationViewModel | 智能推荐/用户反馈 |
| 新人引导 | NewPerson | NewPersonViewModel | 口味画像建立 |
| 个人中心 | Mine | MineViewModel | 用户信息/统计/入口 |
| 编辑资料 | EditProfile | EditProfileViewModel | 头像/用户名修改 |
| 决策成功 | FoodLiked | FoodLikedViewModel | 历史点赞菜品 |
| 建议 | Suggestion, SuggestionDetail, AddSuggestion | SuggestionViewModel, AddSuggestionViewModel | 建议提交/查看 |
| 食物轨迹 | FoodTrack | - | WebView 饮食记录 |
| 图片预览 | ImagePreview | - | 图片浏览 |
| 启动 | Splash | AppViewModel | 应用初始化 |

---

## 核心技术特点

1. **100% Kotlin + Jetpack Compose**: 纯现代 Android 技术栈
2. **MVVM 架构**: 清晰的数据流和状态管理
3. **Paging 3**: 高效的分页加载
4. **Room 缓存**: 离线优先的美食推荐
5. **DataStore**: 类型安全的本地存储
6. **Retrofit + Kotlinx Serialization**: 高效的网络通信
7. **Coil 3**: 现代化的图片加载
8. **Navigation3**: Compose 原生导航