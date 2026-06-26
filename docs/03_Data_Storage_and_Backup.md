# 迹忆（TraceMind）数据存储与备份方案

## 设计目标

迹忆（TraceMind）采用本地优先（Local First）设计理念。

### 核心原则
* 不依赖账号系统
* 不依赖云端同步
* 用户拥有全部数据
* 支持离线使用
* 支持跨设备迁移
* 支持完整导出与导入
* 永不锁定用户数据

用户即使更换手机、卸载应用、恢复出厂设置，也可以通过备份文件恢复全部数据。

---

# 数据存储架构

采用：
```text
SQLite + 文件资源目录
```

架构如下：
```text
AppData/
├── tracemind.db
├── images/
├── audios/
└── cache/
```

---

# SQLite 数据库存储内容

数据库仅存储结构化数据。*(注：根据产品需求，已加入 Folder 目录结构设计)*

## Folder（文件夹/目录）
用于支持多级日记分类（如“学习” -> “日语”）。
```kotlin
data class Folder(
    val id: String,
    val parentId: String?, // 指向父文件夹的 ID，若为 null 则在根目录
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

## Diary（日记）
```kotlin
data class Diary(
    val id: String,
    val folderId: String?, // 所属的文件夹 ID，若为 null 则在根目录
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val mood: String?,
    val weather: String?,
    val tags: List<String>
)
```

## DiaryImage（日记图片）
```kotlin
data class DiaryImage(
    val id: String,
    val diaryId: String,
    val filePath: String
)
```
示例：`images/IMG_20260625_001.jpg`

## DiaryAudio（日记录音）
```kotlin
data class DiaryAudio(
    val id: String,
    val diaryId: String,
    val filePath: String,
    val duration: Long
)
```
示例：`audios/AUDIO_20260625_001.m4a`

## Tag（标签）
```kotlin
data class Tag(
    val id: String,
    val name: String
)
```

## AI Analysis（AI分析）
```kotlin
data class DiaryAnalysis(
    val diaryId: String,
    val summary: String,
    val sentiment: String,
    val keywords: List<String>
)
```

---

# 文件存储规范

## 图片
存储目录：`images/`
支持格式：jpg, jpeg, png, webp
**规范**：数据库仅保存相对路径。不将图片二进制数据存入数据库。

## 录音
存储目录：`audios/`
推荐格式：m4a
**原因**：文件体积小，音质较好，Android/iOS支持完善。
**规范**：数据库仅保存相对路径。不将音频存入数据库。

---

# 备份方案

采用自定义备份文件，扩展名：`.diary`
示例：`我的人生.diary`

## 备份文件结构
本质是无密码的 ZIP 压缩包。
内部结构：
```text
backup.diary
├── tracemind.db
├── images/
├── audios/
└── metadata.json
```

## metadata.json
用于记录备份信息。
```json
{
  "appName": "TraceMind",
  "version": "1.0.0",
  "exportTime": "2026-06-25T18:30:00Z",
  "backupVersion": 1
}
```

---

# 导入与导出流程

## 导出流程
用户点击：`设置 -> 数据管理 -> 导出全部数据`
系统执行：
1. 锁定数据库（确保一致性）
2. 导出 SQLite 文件 (`tracemind.db`)
3. 收集 `images/` 目录文件
4. 收集 `audios/` 目录文件
5. 生成 `metadata.json`
6. 打包为 ZIP
7. 修改扩展名为 `.diary`
8. 输出备份文件 (例如 `TraceMind_Backup_20260625.diary`) 到系统下载或分享目录。

## 导入流程
用户点击：`设置 -> 数据管理 -> 导入备份`
系统执行：
1. 选择 `.diary` 文件
2. 校验文件格式及 `metadata.json`
3. 解压文件到临时缓存目录
4. 校验数据库可用性
5. 恢复图片和录音到 App Data 目录
6. 覆盖恢复 SQLite 数据库
7. 重启或刷新应用完成导入

---

# 设计结论

TraceMind 采用 `SQLite + 文件资源目录 + .diary备份包` 的存储模型。
完美实现：
✓ 无账号
✓ 本地优先
✓ 数据归用户所有
✓ 完整导出与导入
✓ 跨设备迁移
✓ 支持多级目录分类
✓ 长期可维护，且支持未来 AI 能力扩展（无缝兼容备份格式）
