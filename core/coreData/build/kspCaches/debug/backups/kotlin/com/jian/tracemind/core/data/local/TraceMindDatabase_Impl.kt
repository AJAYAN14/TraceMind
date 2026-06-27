package com.jian.tracemind.core.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.jian.tracemind.core.`data`.local.dao.DiaryDao
import com.jian.tracemind.core.`data`.local.dao.DiaryDao_Impl
import com.jian.tracemind.core.`data`.local.dao.FolderDao
import com.jian.tracemind.core.`data`.local.dao.FolderDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TraceMindDatabase_Impl : TraceMindDatabase() {
  private val _folderDao: Lazy<FolderDao> = lazy {
    FolderDao_Impl(this)
  }

  public override val folderDao: FolderDao
    get() = _folderDao.value

  private val _diaryDao: Lazy<DiaryDao> = lazy {
    DiaryDao_Impl(this)
  }

  public override val diaryDao: DiaryDao
    get() = _diaryDao.value

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "41c579bd55b6575642557bde2e1a7931", "06929ea00b7ecfa567057df5c2a38e46") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `folders` (`id` TEXT NOT NULL, `parentId` TEXT, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `diaries` (`id` TEXT NOT NULL, `folderId` TEXT, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `mood` TEXT, `weather` TEXT, `tags` TEXT NOT NULL, `images` TEXT NOT NULL, `audioPath` TEXT, `coverImage` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '41c579bd55b6575642557bde2e1a7931')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `folders`")
        connection.execSQL("DROP TABLE IF EXISTS `diaries`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsFolders: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFolders.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("parentId", TableInfo.Column("parentId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFolders.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFolders: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFolders: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFolders: TableInfo = TableInfo("folders", _columnsFolders, _foreignKeysFolders,
            _indicesFolders)
        val _existingFolders: TableInfo = read(connection, "folders")
        if (!_infoFolders.equals(_existingFolders)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |folders(com.jian.tracemind.core.data.local.entity.FolderEntity).
              | Expected:
              |""".trimMargin() + _infoFolders + """
              |
              | Found:
              |""".trimMargin() + _existingFolders)
        }
        val _columnsDiaries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDiaries.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("folderId", TableInfo.Column("folderId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("mood", TableInfo.Column("mood", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("weather", TableInfo.Column("weather", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("tags", TableInfo.Column("tags", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("images", TableInfo.Column("images", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("audioPath", TableInfo.Column("audioPath", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDiaries.put("coverImage", TableInfo.Column("coverImage", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDiaries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDiaries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDiaries: TableInfo = TableInfo("diaries", _columnsDiaries, _foreignKeysDiaries,
            _indicesDiaries)
        val _existingDiaries: TableInfo = read(connection, "diaries")
        if (!_infoDiaries.equals(_existingDiaries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |diaries(com.jian.tracemind.core.data.local.entity.DiaryEntity).
              | Expected:
              |""".trimMargin() + _infoDiaries + """
              |
              | Found:
              |""".trimMargin() + _existingDiaries)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "folders", "diaries")
  }

  public override fun clearAllTables() {
    super.performClear(false, "folders", "diaries")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(FolderDao::class, FolderDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DiaryDao::class, DiaryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }
}
