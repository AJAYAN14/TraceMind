---
trigger: always_on
---

# Kotlin + Jetpack Compose Agent Rules (Production Grade)

## 0. Core Principle（最高原则）

- 可维护性 > 一切优化 > 性能微优化 > UI美观
- 所有行为必须显式执行，禁止隐式逻辑
- 未经用户确认，禁止生成代码或修改项目结构
- 永远遵循：**分析 → 设计 → 方案确认 → 实现**

---

## 1. Language Rules（语言规则）

- 默认使用中文进行沟通与分析说明
- 所有代码必须使用英文
- 禁止中英文混合变量命名
- 禁止拼音命名
- 禁止非标准缩写（行业通用缩写除外，如 DTO / UI / API）

---

## 2. Mandatory Workflow（强制流程）

任何用户请求必须严格按流程执行：

### Step 1: Requirement Analysis（需求分析）

必须输出：

- 需求重述（确保语义一致）
- 输入 / 输出定义
- 功能边界（做什么 / 不做什么）
- 关键不确定点识别

若存在歧义：

→ 必须提问  
→ 禁止进入下一阶段

---

### Step 2: Task Decomposition（任务拆解）

必须结构化拆分：

- Feature 模块拆分
- UI 层级结构（Compose Screen / Component）
- State 管理拆分（UiState / Event / Effect）
- Domain 用例拆分（UseCase）
- Data 层拆分（Repository / Source）
- 本地 / 网络数据划分

---

### Step 3: Architecture Design（架构设计）

必须提供：

- 架构模式（默认 Clean Architecture + MVVM）
- 分层职责说明
- 数据流向（UI → ViewModel → Domain → Data）
- 核心类设计（ViewModel / UseCase / Repository）
- 依赖关系说明
- 风险点分析（线程 / 状态 / 生命周期）

---

### Step 4: User Confirmation（用户确认）

在用户确认前：

❌ 禁止写代码  
❌ 禁止输出完整实现  
❌ 禁止直接优化或重构建议落地  

只能输出：

- 分析
- 设计
- 方案

---

### Step 5: Implementation（实现阶段）

仅在用户确认后：

- 按模块逐步实现
- 每个 Kotlin 文件必须完整输出
- 必须标明文件路径
- 修改必须说明影响范围
- 禁止“顺手优化”
- 禁止跨范围重构

---

## 3. Code Quality Rules（代码质量）

必须遵守：

- 优先不可变数据（val > var）
- UI 与业务逻辑完全分离
- ViewModel 不直接持有 Context（除非必要）
- 禁止业务逻辑写在 Composable 中
- 使用 StateFlow / SharedFlow 管理状态
- 优先使用 sealed class 表达 UI 状态
- 使用 suspend + Coroutines 管理异步
- 优先 Flow 链式处理，避免 callback 结构

---

## 4. Architecture Rules（架构约束）

默认使用 **Clean Architecture + Feature-first**

```text
core/
data/
domain/
presentation/
features/
shared/
```

### 规则

- domain 层完全独立 Android / Compose
- data 层仅负责数据来源（API / DB）
- presentation 层仅负责 UI + ViewModel
- repository 是唯一数据入口
- 禁止跨层直接访问 data source
- feature 内必须独立闭环

---

## 5. Modular Design Rules（模块化设计规范）

默认采用 **Multi-module 架构**，按功能拆分模块：

### 模块类型

> **注意**：以下模块结构为推荐方案，非硬性要求。实际项目应根据团队规模、项目复杂度、业务需求灵活调整。小型项目可简化为单模块或减少 core 子模块数量。

```text
app/                    # 主应用模块（壳模块）
core/
  ├── coreCommon/       # 公共工具、扩展、基础组件
  ├── coreData/         # 数据层基础设施（网络、数据库基类）
  ├── coreDomain/       # 领域层基础设施（UseCase 基类）
  ├── coreUi/           # UI 基础组件、主题、设计系统
  └── coreNavigation/   # 导航基础设施
feature/
  ├── featureAuth/      # 认证模块
  ├── featureHome/      # 首页模块
  ├── featureProfile/   # 个人中心模块
  └── ...               # 其他功能模块
```

### 模块拆分建议

| 项目规模 | 建议模块结构 | 说明 |
|----------|-------------|------|
| 小型项目（< 10 个功能） | `app` + 单一 `feature` 或无拆分 | 可不拆分 core，直接在 app 中分层 |
| 中型项目（10-30 个功能） | `app` + `core` + `feature` | 按需拆分 core 子模块，feature 按业务拆分 |
| 大型项目（> 30 个功能） | 完整 Multi-module | 完整拆分，考虑动态功能模块（Dynamic Feature） |

### 模块命名规范

- 模块名统一使用驼峰命名法：`featureAuth`、`coreCommon`、`coreData`
- 同一类型模块保持命名一致性
- 前缀约定：
  - `core` + 功能名：`coreCommon`、`coreData`、`coreUi`
  - `feature` + 功能名：`featureAuth`、`featureHome`、`featureProfile`

### 模块依赖规则

- `app` 模块依赖所有 `feature` 和 `core` 模块
- `feature` 模块只依赖 `core` 模块，禁止互相依赖
- `core` 模块之间可按需依赖，但避免循环依赖
- `core:domain` 必须完全独立，不依赖任何 Android SDK

### 模块边界

- 每个模块必须有明确的职责边界
- 模块内部遵循 Clean Architecture 分层
- 模块间通信通过：
  - Navigation 路由
  - Event Bus（谨慎使用）
  - Shared ViewModel（同一功能内）

### 新模块创建流程

1. 明确模块职责边界
2. 确定依赖关系
3. 创建模块目录结构
4. 配置 build.gradle.kts
5. 在 settings.gradle.kts 注册模块

---

## 6. Dependency Injection Rules（Hilt 依赖注入规范）

默认使用 **Hilt** 进行依赖注入：

### 基本规则

- 所有 ViewModel 必须使用 `@HiltViewModel`
- 所有依赖必须通过构造函数注入
- 禁止使用 `@Inject` 注入到字段（field injection）
- 禁止在 Activity / Fragment 中手动创建依赖

### ViewModel 注入

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ...
}
```

### Repository 注入

```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userDao: UserDao
) : AuthRepository {
    // ...
}
```

### Module 配置

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // ...
    }
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
```

### Scope 规则

| Scope | 生命周期 | 使用场景 |
|-------|----------|----------|
| `@Singleton` | 应用级 | Repository、ApiService、Database |
| `@ActivityRetainedScoped` | Activity 重建后保留 | ViewModelStoreOwner 相关 |
| `@ActivityScoped` | Activity 级 | Activity 特定依赖 |
| `@ViewModelScoped` | ViewModel 级 | ViewModel 内部依赖 |
| `@ViewScoped` | View 级 | Compose 特定场景 |

### Module 安装位置

| Component | Module 安装位置 |
|-----------|----------------|
| 网络层 | `SingletonComponent` |
| 数据库 | `SingletonComponent` |
| Repository | `SingletonComponent` |
| UseCase | `SingletonComponent`（无状态）或 `ViewModelComponent`（有状态）|
| ViewModel | 自动管理，无需手动安装 |

### 禁止行为

- ❌ 禁止在 Module 中持有状态
- ❌ 禁止使用 `@Inject` 字段注入
- ❌ 禁止在 Singleton 中持有 Activity/Fragment 引用
- ❌ 禁止跨 Scope 注入不兼容的依赖
- ❌ 禁止在 Composable 中直接创建 ViewModel（使用 `hiltViewModel()`）

### Compose 中使用

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // ...
}
```

### Entry Points（跨模块访问）

当需要从非 Hilt 管理的类中获取依赖时：

```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun authRepository(): AuthRepository
}

// 使用方式
val appContext = context.applicationContext
val hiltEntryPoint = EntryPointAccessors.fromApplication(
    appContext,
    RepositoryEntryPoint::class.java
)
val authRepository = hiltEntryPoint.authRepository()
```

---

## 7. State Management Rules（状态管理）

- 默认使用 ViewModel + StateFlow
- UI 状态必须封装为 immutable UiState
- 单向数据流（UDF）必须严格遵守
- 使用 sealed class 表示 UI Event / Effect
- UI 不持有业务状态
- 禁止在 Composable 中直接处理业务逻辑

---

## 8. Data Rules（数据层）

- 默认 Offline First 架构
- 本地数据库优先：Room
- 网络层统一封装：Retrofit + ApiService
- Repository 是唯一数据访问入口
- 数据模型必须支持 mapping（DTO ↔ Domain）
- UI 不得直接访问 API / DB

---

## 9. UI Rules（Jetpack Compose UI 规范）

- 必须使用 Material 3（M3）
- 禁止随机颜色
- spacing 统一使用：4 / 8 / 12 / 16 / 24 / 32
- Composable 必须具备单一职责
- UI 不允许包含业务逻辑
- 优先拆分为可复用 Composable
- 动画必须克制（≤300ms）
- 避免过度重组（recomposition optimization）

---

## 10. Modification Rules（修改规则）

- 禁止修改未涉及模块
- 修改前必须说明影响范围
- 跨模块变更必须提供迁移方案
- 禁止自动重构
- 禁止“顺手优化”
- 每次修改必须可追踪

---

## 11. Output Rules（输出规则）

- 默认输出完整 Kotlin 文件
- 必须标注文件路径
- 多文件修改必须分块输出
- 代码必须可直接编译运行
- 除非用户要求，否则禁止只输出片段代码

---

## 12. Safety Rules（安全规则）

- 禁止破坏现有架构
- 禁止删除未确认代码
- 禁止大规模重写项目
- 优先稳定性 > 结构优化 > UI美化

---

## 13. Planning Enforcement（规划强制）

任何任务必须先输出：

### Task List

- 明确步骤
- 明确依赖关系
- 明确模块边界
- 明确风险点

---

### Implementation Plan

- 分阶段执行
- 每阶段必须可验证
- 每阶段必须可回滚

---

## 14. Final Hard Constraints（最终约束）

- 必须先分析，再设计，再实现
- 必须任务清单化
- 必须用户确认后才允许编码
- 必须保持架构稳定
- 必须避免隐式行为
- 必须遵循单向数据流（UDF）
