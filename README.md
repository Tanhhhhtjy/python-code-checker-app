# Python代码查重助手 - 项目设计文档

---

## 📋 文档信息

| 项目名称 | Python代码查重助手 (CodeChecker) |
| -------- | -------------------------------- |
| 版本     | v1.0                             |
| 目标平台 | Android 9.0+ (API 28+)           |
| 开发语言 | Kotlin                           |
| 架构模式 | MVVM + Clean Architecture        |
| UI风格   | Material Design 3 (Material You) |

---

## 一、需求分析

### 1.1 项目背景与目标

为北航程序设计课程开发一款本地化的Python代码查重App，用于：

- 维护学术诚信，防止代码抄袭
- 为教师提供便捷的作业管理与查重工具
- 为学生提供自查功能，及时发现问题

### 1.2 用户角色分析

```
┌─────────────────────────────────────────────────────────────┐
│                      用户角色模型                            │
├─────────────────────────┬───────────────────────────────────┤
│        学生 (Student)    │          教师 (Teacher)           │
├─────────────────────────┼───────────────────────────────────┤
│ • 注册/登录账户          │ • 注册/登录账户                    │
│ • 查看作业列表           │ • 创建/管理作业任务                │
│ • 提交Python代码文件     │ • 查看所有学生提交                 │
│ • 查看个人查重历史       │ • 执行批量查重                     │
│ • 查看查重报告详情       │ • 查看完整查重报告                 │
│                         │ • AI智能分析（选做）               │
│                         │ • 历史趋势分析（选做）              │
└─────────────────────────┴───────────────────────────────────┘
```

### 1.3 功能需求清单

#### 必做功能 (Must Have)

| 模块     | 功能点        | 优先级 | 预计工时 |
| -------- | ------------- | ------ | -------- |
| 用户系统 | 学生/教师注册 | P0     | 4h       |
| 用户系统 | 登录/登出     | P0     | 3h       |
| 用户系统 | 权限管理      | P0     | 2h       |
| 作业管理 | 教师创建作业  | P0     | 4h       |
| 作业管理 | 作业列表展示  | P0     | 3h       |
| 代码提交 | 单文件上传    | P0     | 4h       |
| 代码提交 | 多文件上传    | P1     | 3h       |
| 代码提交 | 提交记录存储  | P0     | 3h       |
| 查重引擎 | Token词法分析 | P0     | 8h       |
| 查重引擎 | 相似度算法    | P0     | 6h       |
| 查重引擎 | 两两比对逻辑  | P0     | 4h       |
| 报告展示 | 相似度列表    | P0     | 4h       |
| 报告展示 | 代码高亮对比  | P0     | 6h       |

#### 选做功能 (Should Have)

| 模块     | 功能点        | 优先级 | 预计工时 |
| -------- | ------------- | ------ | -------- |
| AI分析   | 大模型API集成 | P2     | 6h       |
| AI分析   | 智能原因分析  | P2     | 4h       |
| 批量处理 | 一键批量查重  | P2     | 4h       |
| 历史分析 | 趋势图表      | P3     | 5h       |
| 历史分析 | 异常检测提示  | P3     | 3h       |

### 1.4 用例图

```
┌────────────────────────────────────────────────────────────────┐
│                        CodeChecker 系统                        │
│                                                                │
│  ┌─────────┐                              ┌─────────┐          │
│  │  学生   │                              │  教师   │          │
│  └────┬────┘                              └────┬────┘          │
│       │                                        │               │
│       ├──────► 注册账户 ◄──────────────────────┤               │
│       ├──────► 登录系统 ◄──────────────────────┤               │
│       │                                        │               │
│       ├──────► 查看作业列表 ◄──────────────────┤               │
│       │                                        │               │
│       ├──────► 提交代码文件                    │               │
│       │                                        │               │
│       ├──────► 查看个人查重历史                │               │
│       │                                        │               │
│       ├──────► 查看查重报告 ◄──────────────────┤               │
│       │                                        │               │
│       │                    创建作业任务 ◄──────┤               │
│       │                                        │               │
│       │                    查看所有提交 ◄──────┤               │
│       │                                        │               │
│       │                    执行批量查重 ◄──────┤               │
│       │                                        │               │
│       │                    AI智能分析 ◄────────┤ (选做)        │
│       │                                        │               │
└───────┴────────────────────────────────────────┴───────────────┘
```

---

## 二、架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Screen    │  │  ViewModel  │  │   State     │              │
│  │  (Compose)  │◄─┤             │◄─┤   (Flow)    │              │
│  └─────────────┘  └──────┬──────┘  └─────────────┘              │
├──────────────────────────┼──────────────────────────────────────┤
│                        Domain Layer                              │
│  ┌─────────────┐  ┌──────┴──────┐  ┌─────────────┐              │
│  │   UseCase   │  │  Repository │  │   Entity    │              │
│  │             │──┤  Interface  │  │             │              │
│  └─────────────┘  └──────┬──────┘  └─────────────┘              │
├──────────────────────────┼──────────────────────────────────────┤
│                         Data Layer                               │
│  ┌─────────────┐  ┌──────┴──────┐  ┌─────────────┐              │
│  │    Room     │  │ Repository  │  │  External   │              │
│  │  Database   │◄─┤    Impl     │─►│    API      │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 项目模块结构

```
app/
├── src/main/java/com/example/codechecker/
│   ├── CodeCheckerApp.kt                 # Application类
│   ├── MainActivity.kt                   # 主Activity
│   │
│   ├── data/                             # 数据层
│   │   ├── local/
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt        # Room数据库
│   │   │   │   └── Converters.kt         # 类型转换器
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── AssignmentDao.kt
│   │   │   │   ├── SubmissionDao.kt
│   │   │   │   └── ReportDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       ├── AssignmentEntity.kt
│   │   │       ├── SubmissionEntity.kt
│   │   │       └── ReportEntity.kt
│   │   ├── remote/
│   │   │   └── AIApiService.kt           # AI API调用
│   │   └── repository/
│   │       ├── UserRepositoryImpl.kt
│   │       ├── AssignmentRepositoryImpl.kt
│   │       └── ReportRepositoryImpl.kt
│   │
│   ├── domain/                           # 领域层
│   │   ├── model/                        # 领域模型
│   │   │   ├── User.kt
│   │   │   ├── Assignment.kt
│   │   │   ├── Submission.kt
│   │   │   └── PlagiarismReport.kt
│   │   ├── repository/                   # 仓库接口
│   │   │   ├── UserRepository.kt
│   │   │   ├── AssignmentRepository.kt
│   │   │   └── ReportRepository.kt
│   │   └── usecase/                      # 用例
│   │       ├── auth/
│   │       │   ├── LoginUseCase.kt
│   │       │   └── RegisterUseCase.kt
│   │       ├── assignment/
│   │       │   ├── CreateAssignmentUseCase.kt
│   │       │   └── GetAssignmentsUseCase.kt
│   │       └── plagiarism/
│   │           ├── RunPlagiarismCheckUseCase.kt
│   │           └── GetReportUseCase.kt
│   │
│   ├── algorithm/                        # 查重算法
│   │   ├── tokenizer/
│   │   │   └── PythonTokenizer.kt        # Python词法分析器
│   │   ├── similarity/
│   │   │   ├── SimilarityCalculator.kt   # 相似度计算接口
│   │   │   ├── JaccardSimilarity.kt      # Jaccard相似度
│   │   │   └── LCSSimilarity.kt          # LCS相似度
│   │   └── engine/
│   │       └── PlagiarismEngine.kt       # 查重引擎
│   │
│   ├── ui/                               # 表现层
│   │   ├── navigation/
│   │   │   └── NavGraph.kt               # 导航图
│   │   ├── theme/
│   │   │   ├── Color.kt                      # Material3色板（含动态色）
│   │   │   ├── Theme.kt                      # Material3主题配置
│   │   │   └── Type.kt                       # 字体排版（M3 Typography）
│   │   ├── components/                   # 公共组件
│   │   │   ├── CodeHighlighter.kt
│   │   │   ├── SimilarityChart.kt
│   │   │   └── LoadingIndicator.kt
│   │   └── screens/
│   │       ├── auth/
│   │       │   ├── LoginScreen.kt
│   │       │   ├── RegisterScreen.kt
│   │       │   └── AuthViewModel.kt
│   │       ├── home/
│   │       │   ├── HomeScreen.kt
│   │       │   └── HomeViewModel.kt
│   │       ├── assignment/
│   │       │   ├── AssignmentListScreen.kt
│   │       │   ├── AssignmentDetailScreen.kt
│   │       │   ├── CreateAssignmentScreen.kt
│   │       │   └── AssignmentViewModel.kt
│   │       ├── submission/
│   │       │   ├── SubmitCodeScreen.kt
│   │       │   └── SubmissionViewModel.kt
│   │       └── report/
│   │           ├── ReportListScreen.kt
│   │           ├── ReportDetailScreen.kt
│   │           ├── CodeCompareScreen.kt
│   │           └── ReportViewModel.kt
│   │
│   └── util/                             # 工具类
│       ├── HashUtil.kt                   # 密码加密
│       ├── FileUtil.kt                   # 文件处理
│       └── DateUtil.kt                   # 日期格式化
│
├── src/main/res/
│   ├── values/
│   │   ├── strings.xml
│   │   └── colors.xml
│   └── ...
│
└── build.gradle.kts
```

### 2.3 技术选型

| 类别         | 技术方案                                | 选择理由                    |
| ------------ | --------------------------------------- | --------------------------- |
| **UI框架**   | Jetpack Compose                         | 现代化声明式UI，代码简洁    |
| **UI库**     | Material 3 (androidx.compose.material3) | 遵循MD3规范，动态色兼容     |
| **架构模式** | MVVM + Clean Architecture               | 职责分离，易于测试和维护    |
| **数据库**   | Room                                    | 官方ORM，类型安全，协程支持 |
| **异步处理** | Kotlin Coroutines + Flow                | 官方推荐，简洁高效          |
| **依赖注入** | Hilt                                    | 官方推荐，编译时检查        |
| **导航**     | Navigation Compose                      | 官方组件，类型安全          |
| **网络请求** | OkHttp + Retrofit                       | 成熟稳定，拦截器机制        |
| **图表**     | MPAndroidChart / Vico                   | 功能丰富，社区活跃          |
| **JSON解析** | Kotlinx.serialization                   | 官方库，Kotlin优化          |

---

## 三、数据库设计

### 3.1 ER图

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│      User       │       │   Assignment    │       │   Submission    │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ PK  id          │       │ PK  id          │       │ PK  id          │
│     username    │       │ FK  teacherId   │───┐   │ FK  studentId   │──┐
│     passwordHash│       │     title       │   │   │ FK  assignmentId│──┼─┐
│     displayName │       │     description │   │   │     fileName    │  │ │
│     role        │◄──────│     deadline    │   │   │     codeContent │  │ │
│     createdAt   │       │     createdAt   │   │   │     submittedAt │  │ │
└─────────────────┘       └─────────────────┘   │   └─────────────────┘  │ │
        ▲                                       │            ▲           │ │
        │                                       │            │           │ │
        │                                       └────────────┼───────────┘ │
        │                                                    │             │
        │         ┌─────────────────┐       ┌────────────────┴─────────┐   │
        │         │ PlagiarismReport│       │      SimilarityPair      │   │
        │         ├─────────────────┤       ├──────────────────────────┤   │
        │         │ PK  id          │       │ PK  id                   │   │
        │         │ FK  assignmentId│◄──────│ FK  reportId             │   │
        │         │ FK  executorId  │───────│ FK  submission1Id        │◄──┘
        │         │     status      │       │ FK  submission2Id        │◄───
        └─────────│     createdAt   │       │     similarityScore      │
                  │     completedAt │       │     highlightData (JSON) │
                  └─────────────────┘       │     aiAnalysis (可选)    │
                                            └──────────────────────────┘
```

### 3.2 表结构详细设计

#### 3.2.1 用户表 (users)

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/data/local/entity/UserEntity.kt

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "username")
    val username: String,                    // 登录用户名，唯一
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,                // SHA-256加密后的密码
    
    @ColumnInfo(name = "display_name")
    val displayName: String,                 // 显示名称
    
    @ColumnInfo(name = "role")
    val role: String,                        // "STUDENT" 或 "TEACHER"
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

#### 3.2.2 作业表 (assignments)

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/data/local/entity/AssignmentEntity.kt

@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["teacher_id"])]
)
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "teacher_id")
    val teacherId: Long,                     // 创建教师ID
    
    @ColumnInfo(name = "title")
    val title: String,                       // 作业标题
    
    @ColumnInfo(name = "description")
    val description: String,                 // 作业描述
    
    @ColumnInfo(name = "deadline")
    val deadline: Long?,                     // 截止时间（可选）
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

#### 3.2.3 提交记录表 (submissions)

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/data/local/entity/SubmissionEntity.kt

@Entity(
    tableName = "submissions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["student_id"]),
        Index(value = ["assignment_id"]),
        Index(value = ["student_id", "assignment_id"])
    ]
)
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "student_id")
    val studentId: Long,                     // 提交学生ID
    
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,                  // 关联作业ID
    
    @ColumnInfo(name = "file_name")
    val fileName: String,                    // 原始文件名
    
    @ColumnInfo(name = "code_content")
    val codeContent: String,                 // 代码内容
    
    @ColumnInfo(name = "code_hash")
    val codeHash: String,                    // 代码内容的MD5，用于快速判重
    
    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long = System.currentTimeMillis()
)
```

#### 3.2.4 查重报告表 (plagiarism_reports)

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/data/local/entity/ReportEntity.kt

@Entity(
    tableName = "plagiarism_reports",
    foreignKeys = [
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignment_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["executor_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["assignment_id"]),
        Index(value = ["executor_id"])
    ]
)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,                  // 关联作业
    
    @ColumnInfo(name = "executor_id")
    val executorId: Long?,                   // 执行查重的用户
    
    @ColumnInfo(name = "status")
    val status: String,                      // PENDING, RUNNING, COMPLETED, FAILED
    
    @ColumnInfo(name = "total_submissions")
    val totalSubmissions: Int,               // 参与比对的提交数
    
    @ColumnInfo(name = "total_pairs")
    val totalPairs: Int,                     // 比对对数
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)
```

#### 3.2.5 相似度对表 (similarity_pairs)

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/data/local/entity/SimilarityPairEntity.kt

@Entity(
    tableName = "similarity_pairs",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["report_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubmissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["submission1_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubmissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["submission2_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["report_id"]),
        Index(value = ["submission1_id"]),
        Index(value = ["submission2_id"]),
        Index(value = ["similarity_score"])
    ]
)
data class SimilarityPairEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "report_id")
    val reportId: Long,                      // 关联报告
    
    @ColumnInfo(name = "submission1_id")
    val submission1Id: Long,                 // 提交1
    
    @ColumnInfo(name = "submission2_id")
    val submission2Id: Long,                 // 提交2
    
    @ColumnInfo(name = "similarity_score")
    val similarityScore: Double,             // 相似度百分比 0.0-100.0
    
    @ColumnInfo(name = "highlight_data")
    val highlightData: String,               // JSON格式的高亮数据
    
    @ColumnInfo(name = "ai_analysis")
    val aiAnalysis: String? = null           // AI分析结果（选做）
)
```

---

## 四、核心算法设计

### 4.1 查重引擎架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      PlagiarismEngine                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │ Code Input 1 │    │ Code Input 2 │    │ Code Input N │      │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘      │
│         │                   │                   │               │
│         ▼                   ▼                   ▼               │
│  ┌─────────────────────────────────────────────────────┐       │
│  │              PythonTokenizer (词法分析器)             │       │
│  │  • 移除注释和空白                                     │       │
│  │  • 提取关键字、标识符、运算符、字面量                  │       │
│  │  • 标准化变量名（可选）                               │       │
│  └─────────────────────────┬───────────────────────────┘       │
│                            │                                    │
│                            ▼                                    │
│  ┌─────────────────────────────────────────────────────┐       │
│  │              Token Sequences                         │       │
│  │  [DEF, ID, LPAREN, ID, RPAREN, COLON, ...]          │       │
│  └─────────────────────────┬───────────────────────────┘       │
│                            │                                    │
│         ┌──────────────────┼──────────────────┐                │
│         ▼                  ▼                  ▼                │
│  ┌────────────┐    ┌────────────┐    ┌────────────┐           │
│  │  Jaccard   │    │    LCS     │    │  N-gram    │           │
│  │ Similarity │    │ Similarity │    │ Similarity │           │
│  └─────┬──────┘    └─────┬──────┘    └─────┬──────┘           │
│        │                 │                 │                   │
│        └────────────┬────┴────────────────┘                   │
│                     ▼                                          │
│  ┌─────────────────────────────────────────────────────┐       │
│  │           Weighted Score Calculator                  │       │
│  │     finalScore = w1*Jaccard + w2*LCS + w3*Ngram     │       │
│  └─────────────────────────┬───────────────────────────┘       │
│                            ▼                                    │
│  ┌─────────────────────────────────────────────────────┐       │
│  │              Similarity Report                       │       │
│  └─────────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Python词法分析器设计

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/algorithm/tokenizer/PythonTokenizer.kt

/**
 * Python代码词法分析器
 * 将Python源代码转换为Token序列，用于后续相似度计算
 */
class PythonTokenizer {
    
    // Token类型枚举
    enum class TokenType {
        KEYWORD,        // 关键字: def, class, if, for, etc.
        IDENTIFIER,     // 标识符: 变量名、函数名
        OPERATOR,       // 运算符: +, -, *, /, ==, etc.
        NUMBER,         // 数字字面量
        STRING,         // 字符串字面量
        DELIMITER,      // 分隔符: (, ), [, ], {, }, :, ,
        NEWLINE,        // 换行（Python中有意义）
        INDENT,         // 缩进
        DEDENT          // 取消缩进
    }
    
    data class Token(
        val type: TokenType,
        val value: String,
        val line: Int,
        val column: Int
    )
    
    // Python关键字集合
    private val keywords = setOf(
        "False", "None", "True", "and", "as", "assert", "async", "await",
        "break", "class", "continue", "def", "del", "elif", "else", 
        "except", "finally", "for", "from", "global", "if", "import",
        "in", "is", "lambda", "nonlocal", "not", "or", "pass", "raise",
        "return", "try", "while", "with", "yield"
    )
    
    /**
     * 主分析函数：将代码转换为Token列表
     */
    fun tokenize(code: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val lines = code.lines()
        
        for ((lineNum, line) in lines.withIndex()) {
            // 跳过纯注释行
            val trimmed = line.trim()
            if (trimmed.startsWith("#") || trimmed.isEmpty()) continue
            
            // 处理缩进
            val indent = line.takeWhile { it == ' ' || it == '\t' }.length
            if (indent > 0) {
                tokens.add(Token(TokenType.INDENT, indent.toString(), lineNum, 0))
            }
            
            // 移除行内注释
            val codeWithoutComment = removeInlineComment(line)
            
            // 词法分析
            tokens.addAll(tokenizeLine(codeWithoutComment, lineNum))
        }
        
        return tokens
    }
    
    /**
     * 分析单行代码
     */
    private fun tokenizeLine(line: String, lineNum: Int): List<Token> {
        val tokens = mutableListOf<Token>()
        var pos = 0
        
        while (pos < line.length) {
            // 跳过空白
            while (pos < line.length && line[pos].isWhitespace()) pos++
            if (pos >= line.length) break
            
            val char = line[pos]
            
            when {
                // 字符串
                char == '"' || char == '\'' -> {
                    val (token, newPos) = extractString(line, pos, lineNum)
                    tokens.add(token)
                    pos = newPos
                }
                // 数字
                char.isDigit() -> {
                    val (token, newPos) = extractNumber(line, pos, lineNum)
                    tokens.add(token)
                    pos = newPos
                }
                // 标识符或关键字
                char.isLetter() || char == '_' -> {
                    val (token, newPos) = extractIdentifier(line, pos, lineNum)
                    tokens.add(token)
                    pos = newPos
                }
                // 运算符或分隔符
                else -> {
                    val (token, newPos) = extractOperatorOrDelimiter(line, pos, lineNum)
                    if (token != null) tokens.add(token)
                    pos = newPos
                }
            }
        }
        
        return tokens
    }
    
    // ... 其他辅助方法
}
```

### 4.3 相似度算法实现

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/algorithm/similarity/SimilarityCalculator.kt

/**
 * 相似度计算器接口
 */
interface SimilarityCalculator {
    fun calculate(tokens1: List<Token>, tokens2: List<Token>): Double
}

/**
 * Jaccard相似度计算
 * 基于Token集合的交集/并集比值
 */
class JaccardSimilarity : SimilarityCalculator {
    override fun calculate(tokens1: List<Token>, tokens2: List<Token>): Double {
        if (tokens1.isEmpty() && tokens2.isEmpty()) return 100.0
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0
        
        // 转换为特征集合（类型+值）
        val set1 = tokens1.map { "${it.type}:${it.value}" }.toSet()
        val set2 = tokens2.map { "${it.type}:${it.value}" }.toSet()
        
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        
        return (intersection.toDouble() / union) * 100
    }
}

/**
 * LCS (最长公共子序列) 相似度
 * 更能捕捉代码结构相似性
 */
class LCSSimilarity : SimilarityCalculator {
    override fun calculate(tokens1: List<Token>, tokens2: List<Token>): Double {
        if (tokens1.isEmpty() && tokens2.isEmpty()) return 100.0
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0
        
        val m = tokens1.size
        val n = tokens2.size
        
        // 动态规划计算LCS长度
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 1..m) {
            for (j in 1..n) {
                if (tokenEquals(tokens1[i-1], tokens2[j-1])) {
                    dp[i][j] = dp[i-1][j-1] + 1
                } else {
                    dp[i][j] = maxOf(dp[i-1][j], dp[i][j-1])
                }
            }
        }
        
        val lcsLength = dp[m][n]
        val maxLength = maxOf(m, n)
        
        return (lcsLength.toDouble() / maxLength) * 100
    }
    
    private fun tokenEquals(t1: Token, t2: Token): Boolean {
        // 比较Token类型，对于标识符只比较类型不比较值（忽略变量名）
        return if (t1.type == TokenType.IDENTIFIER && t2.type == TokenType.IDENTIFIER) {
            true // 标识符类型相同即可，忽略具体命名
        } else {
            t1.type == t2.type && t1.value == t2.value
        }
    }
}

/**
 * 综合相似度计算器
 * 加权组合多种算法结果
 */
class WeightedSimilarityCalculator(
    private val jaccardWeight: Double = 0.4,
    private val lcsWeight: Double = 0.6
) : SimilarityCalculator {
    
    private val jaccard = JaccardSimilarity()
    private val lcs = LCSSimilarity()
    
    override fun calculate(tokens1: List<Token>, tokens2: List<Token>): Double {
        val jaccardScore = jaccard.calculate(tokens1, tokens2)
        val lcsScore = lcs.calculate(tokens1, tokens2)
        
        return jaccardWeight * jaccardScore + lcsWeight * lcsScore
    }
}
```

### 4.4 查重引擎实现

```kotlin
// 文件路径：app/src/main/java/com/example/codechecker/algorithm/engine/PlagiarismEngine.kt

/**
 * 代码查重引擎
 * 负责协调词法分析和相似度计算，执行两两比对
 */
class PlagiarismEngine(
    private val tokenizer: PythonTokenizer = PythonTokenizer(),
    private val similarityCalculator: SimilarityCalculator = WeightedSimilarityCalculator()
) {
    
    data class ComparisonResult(
        val submission1Id: Long,
        val submission2Id: Long,
        val similarity: Double,
        val matchedRegions: List<MatchedRegion>
    )
    
    data class MatchedRegion(
        val start1: Int,
        val end1: Int,
        val start2: Int,
        val end2: Int
    )
    
    /**
     * 对所有提交进行两两比对
     * @param submissions 提交列表 (id -> code content)
     * @param progressCallback 进度回调
     * @return 比对结果列表
     */
    suspend fun runComparison(
        submissions: Map<Long, String>,
        progressCallback: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): List<ComparisonResult> = withContext(Dispatchers.Default) {
        
        val results = mutableListOf<ComparisonResult>()
        val ids = submissions.keys.toList()
        val totalPairs = ids.size * (ids.size - 1) / 2
        var currentPair = 0
        
        // 预先对所有代码进行词法分析（优化性能）
        val tokenCache = submissions.mapValues { (_, code) ->
            tokenizer.tokenize(code)
        }
        
        // 两两比对
        for (i in ids.indices) {
            for (j in i + 1 until ids.size) {
                val id1 = ids[i]
                val id2 = ids[j]
                
                val tokens1 = tokenCache[id1] ?: emptyList()
                val tokens2 = tokenCache[id2] ?: emptyList()
                
                val similarity = similarityCalculator.calculate(tokens1, tokens2)
                val matchedRegions = findMatchedRegions(tokens1, tokens2)
                
                results.add(ComparisonResult(
                    submission1Id = id1,
                    submission2Id = id2,
                    similarity = similarity,
                    matchedRegions = matchedRegions
                ))
                
                currentPair++
                progressCallback(currentPair, totalPairs)
            }
        }
        
        // 按相似度降序排列
        results.sortedByDescending { it.similarity }
    }
    
    /**
     * 查找匹配区域（用于高亮显示）
     */
    private fun findMatchedRegions(
        tokens1: List<Token>, 
        tokens2: List<Token>
    ): List<MatchedRegion> {
        // 使用滑动窗口或LCS回溯找出匹配区域
        // 简化实现：返回连续匹配的Token区间
        // ...
        return emptyList() // 具体实现较复杂，后续详细展开
    }
}
```

---

## 五、UI设计

### 5.1 页面流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                         App Navigation                           │
└─────────────────────────────────────────────────────────────────┘

                            ┌───────────┐
                            │  启动页   │
                            │  Splash   │
                            └─────┬─────┘
                                  │
                                  ▼
                            ┌───────────┐
                   ┌────────┤   登录    ├────────┐
                   │        │  Login    │        │
                   │        └───────────┘        │
                   │              │              │
                   ▼              │              ▼
            ┌───────────┐        │       ┌───────────┐
            │   注册    │        │       │   首页    │
            │ Register  │────────┴──────►│   Home    │
            └───────────┘                └─────┬─────┘
                                               │
                     ┌─────────────────────────┼─────────────────────────┐
                     │                         │                         │
                     ▼                         ▼                         ▼
              ┌─────────────┐          ┌─────────────┐          ┌─────────────┐
              │  作业列表   │          │  查重历史   │          │  个人设置   │
              │ Assignments │          │  History    │          │  Profile    │
              └──────┬──────┘          └──────┬──────┘          └─────────────┘
                     │                        │
         ┌───────────┼───────────┐            │
         │           │           │            │
         ▼           ▼           ▼            ▼
  ┌───────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐
  │ 作业详情  │ │创建作业 │ │提交代码 │ │  报告详情   │
  │  Detail   │ │ Create  │ │ Submit  │ │ReportDetail │
  └─────┬─────┘ │(教师)   │ │(学生)   │ └──────┬──────┘
        │       └─────────┘ └─────────┘        │
        │                                      │
        ▼                                      ▼
  ┌───────────┐                         ┌───────────┐
  │ 查重报告  │                         │ 代码对比  │
  │  Report   │────────────────────────►│  Compare  │
  └───────────┘                         └───────────┘
```

### 5.2 主要页面设计

#### 5.2.1 登录页面

```
┌─────────────────────────────────────┐
│           CodeChecker               │
│         Python代码查重助手          │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 👤 用户名                    │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 🔒 密码                      │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │          登  录              │   │
│  └─────────────────────────────┘   │
│                                     │
│        没有账号？立即注册           │
└─────────────────────────────────────┘
```

#### 5.2.2 首页（教师视图）

```
┌─────────────────────────────────────┐
│ ≡  首页                     👤 张老师 │
├─────────────────────────────────────┤
│                                     │
│  📊 数据概览                        │
│  ┌─────────┐ ┌─────────┐ ┌───────┐ │
│  │  作业数  │ │ 提交数  │ │待查重 │ │
│  │    5    │ │   128   │ │   2   │ │
│  └─────────┘ └─────────┘ └───────┘ │
│                                     │
│  📝 我的作业                    +   │
│  ┌─────────────────────────────┐   │
│  │ 作业1: Python基础练习        │   │
│  │ 截止: 2024-01-15  提交: 32   │   │
│  │ [查看详情]  [开始查重]       │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ 作业2: 算法设计实验          │   │
│  │ 截止: 2024-01-20  提交: 28   │   │
│  │ [查看详情]  [开始查重]       │   │
│  └─────────────────────────────┘   │
│                                     │
├─────────────────────────────────────┤
│  🏠首页    📋作业    📈报告    👤我的 │
└─────────────────────────────────────┘
```

#### 5.2.3 查重报告页面

```
┌─────────────────────────────────────┐
│ ←  查重报告                         │
├─────────────────────────────────────┤
│                                     │
│  作业: Python基础练习               │
│  执行时间: 2024-01-15 14:30        │
│  比对数: 32份代码，496对            │
│                                     │
│  📊 相似度分布                      │
│  ┌─────────────────────────────┐   │
│  │     ████                    │   │
│  │     ████ ██                 │   │
│  │ ██  ████ ████ ██            │   │
│  │ 0-20 20-40 40-60 60-80 80+  │   │
│  └─────────────────────────────┘   │
│                                     │
│  ⚠️ 高相似度警告 (>60%)            │
│  ┌─────────────────────────────┐   │
│  │ 🔴 张三 ↔ 李四     87.5%    │   │
│  │    [查看对比]  [AI分析]     │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ 🟠 王五 ↔ 赵六     72.3%    │   │
│  │    [查看对比]  [AI分析]     │   │
│  └─────────────────────────────┘   │
│                                     │
│  📋 完整列表                    ▼   │
└─────────────────────────────────────┘
```

#### 5.2.4 代码对比页面

```
┌─────────────────────────────────────┐
│ ←  代码对比         相似度: 87.5%   │
├─────────────────────────────────────┤
│  张三              │  李四          │
│  main.py           │  solution.py   │
├──────────────────┬──────────────────┤
│ def fibonacci(n):│ def fib(n):     │
│   if n <= 1:     │   if n <= 1:    │
│     return n     │     return n    │
│ █ result = []    │ █ res = []      │
│ █ a, b = 0, 1    │ █ a, b = 0, 1   │
│ █ for i in ran.. │ █ for i in ra.. │
│ █   result.app.. │ █   res.append..│
│ █   a, b = b, a..│ █   a, b = b,.. │
│ █ return result  │ █ return res    │
│                  │                  │
│ # 测试代码        │ # test          │
│ print(fibonacci..│ print(fib(10))  │
├──────────────────┴──────────────────┤
│  █ 高亮表示相似代码段               │
│                                     │
│  🤖 AI分析结果:                     │
│  ┌─────────────────────────────┐   │
│  │ 判断: 高度疑似抄袭           │   │
│  │ 原因: 算法逻辑完全一致，仅    │   │
│  │      变量命名不同            │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## 六、项目计划

### 6.1 里程碑规划

```
┌────────────────────────────────────────────────────────────────────────┐
│                           项目开发计划 (21天)                           │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  阶段一: 项目启动 (Day 1-2)                                            │
│  ════════════════════                                                  │
│  □ 需求分析与功能清单                                                   │
│  □ 技术选型确认                                                        │
│  □ 项目结构搭建                                                        │
│                                                                        │
│  阶段二: 基础架构 (Day 3-4)                                            │
│  ════════════════════                                                  │
│  □ MVVM架构搭建                                                        │
│  □ Room数据库设计与实现                                                 │
│  □ 导航框架配置                                                        │
│  □ 依赖注入配置(Hilt)                                                  │
│                                                                        │
│  阶段三: 用户模块 (Day 5-6)                                            │
│  ════════════════════                                                  │
│  □ 登录/注册UI                                                         │
│  □ 用户认证逻辑                                                        │
│  □ 权限管理                                                            │
│  □ Session管理                                                         │
│                                                                        │
│  阶段四: 作业管理 (Day 7-9)                                            │
│  ════════════════════                                                  │
│  □ 作业CRUD功能                                                        │
│  □ 代码文件上传                                                        │
│  □ 提交记录管理                                                        │
│                                                                        │
│  阶段五: 查重引擎 (Day 10-12)  ⭐ 核心                                  │
│  ════════════════════════════                                          │
│  □ Python词法分析器                                                    │
│  □ 相似度算法实现                                                      │
│  □ 两两比对引擎                                                        │
│  □ 性能优化                                                            │
│                                                                        │
│  阶段六: 报告展示 (Day 13-14)                                          │
│  ════════════════════                                                  │
│  □ 报告列表页面                                                        │
│  □ 相似度图表                                                          │
│  □ 代码高亮对比                                                        │
│                                                                        │
│  阶段七: 选做功能 (Day 15-18)                                          │
│  ════════════════════                                                  │
│  □ AI分析集成                                                          │
│  □ 批量查重                                                            │
│  □ 历史趋势                                                            │
│                                                                        │
│  阶段八: 测试优化 (Day 19-20)                                          │
│  ════════════════════                                                  │
│  □ 单元测试                                                            │
│  □ 集成测试                                                            │
│  □ Bug修复                                                             │
│  □ 性能优化                                                            │
│                                                                        │
│  阶段九: 验收准备 (Day 21)                                             │
│  ════════════════════                                                  │
│  □ 功能自检                                                            │
│  □ 文档整理                                                            │
│  □ 演示准备                                                            │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 6.2 甘特图

```
| 任务           | Day 1-2 | 3-4  | 5-6  | 7-9  | 10-12  | 13-14 | 15-18  | 19-20 | 21  |
| -------------- | ------- | ---- | ---- | ---- | ------ | ----- | ------ | ----- | --- |
| 需求分析与规划 | ████    |      |      |      |        |       |        |       |
| 架构设计与搭建 |         | ████ |      |      |        |       |        |       |
| 用户系统开发   |         |      | ████ |      |        |       |        |       |
| 作业管理开发   |         |      |      | ████ |        |       |        |       |
| 查重引擎开发   |         |      |      |      | ██████ |       |        |       |
| 报告展示开发   |         |      |      |      |        | ████  |        |       |
| 选做功能开发   |         |      |      |      |        |       | ██████ |       |
| 测试与优化     |         |      |      |      |        |       |        | ████  |
| 验收准备       |         |      |      |      |        |       |        |       | ██  |
```

---

## 七、依赖配置

### 7.1 build.gradle.kts (Project)

```kotlin
// Top-level build file
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}
```

### 7.2 build.gradle.kts (Module: app)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.example.codechecker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.codechecker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    
    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Network (for AI API)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

---

## 八、风险评估与应对

| 风险               | 可能性 | 影响 | 应对措施                               |
| ------------------ | ------ | ---- | -------------------------------------- |
| 查重算法效果不理想 | 中     | 高   | 准备多种算法备选，加权组合             |
| 大文件性能问题     | 中     | 中   | 分块处理，使用协程异步                 |
| AI API调用失败     | 中     | 低   | 降级策略，缓存结果                     |
| 开发时间不足       | 中     | 高   | 优先完成必做功能，选做功能根据进度调整 |
| 数据库迁移问题     | 低     | 中   | 设计好版本迁移策略                     |

---
