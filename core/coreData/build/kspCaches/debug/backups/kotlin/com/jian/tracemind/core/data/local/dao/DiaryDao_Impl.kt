package com.jian.tracemind.core.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jian.tracemind.core.`data`.local.entity.DiaryEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DiaryDao_Impl(
  __db: RoomDatabase,
) : DiaryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDiaryEntity: EntityInsertAdapter<DiaryEntity>

  private val __updateAdapterOfDiaryEntity: EntityDeleteOrUpdateAdapter<DiaryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDiaryEntity = object : EntityInsertAdapter<DiaryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `diaries` (`id`,`folderId`,`title`,`content`,`createdAt`,`updatedAt`,`mood`,`weather`,`tags`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DiaryEntity) {
        statement.bindText(1, entity.id)
        val _tmpFolderId: String? = entity.folderId
        if (_tmpFolderId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpFolderId)
        }
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.content)
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
        val _tmpMood: String? = entity.mood
        if (_tmpMood == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpMood)
        }
        val _tmpWeather: String? = entity.weather
        if (_tmpWeather == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpWeather)
        }
        statement.bindText(9, entity.tags)
      }
    }
    this.__updateAdapterOfDiaryEntity = object : EntityDeleteOrUpdateAdapter<DiaryEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `diaries` SET `id` = ?,`folderId` = ?,`title` = ?,`content` = ?,`createdAt` = ?,`updatedAt` = ?,`mood` = ?,`weather` = ?,`tags` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DiaryEntity) {
        statement.bindText(1, entity.id)
        val _tmpFolderId: String? = entity.folderId
        if (_tmpFolderId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpFolderId)
        }
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.content)
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
        val _tmpMood: String? = entity.mood
        if (_tmpMood == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpMood)
        }
        val _tmpWeather: String? = entity.weather
        if (_tmpWeather == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpWeather)
        }
        statement.bindText(9, entity.tags)
        statement.bindText(10, entity.id)
      }
    }
  }

  public override suspend fun insertDiary(diary: DiaryEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfDiaryEntity.insert(_connection, diary)
  }

  public override suspend fun updateDiary(diary: DiaryEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfDiaryEntity.handle(_connection, diary)
  }

  public override fun getAllDiaries(): Flow<List<DiaryEntity>> {
    val _sql: String = "SELECT * FROM diaries"
    return createFlow(__db, false, arrayOf("diaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMood: Int = getColumnIndexOrThrow(_stmt, "mood")
        val _columnIndexOfWeather: Int = getColumnIndexOrThrow(_stmt, "weather")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<DiaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiaryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMood: String?
          if (_stmt.isNull(_columnIndexOfMood)) {
            _tmpMood = null
          } else {
            _tmpMood = _stmt.getText(_columnIndexOfMood)
          }
          val _tmpWeather: String?
          if (_stmt.isNull(_columnIndexOfWeather)) {
            _tmpWeather = null
          } else {
            _tmpWeather = _stmt.getText(_columnIndexOfWeather)
          }
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _item =
              DiaryEntity(_tmpId,_tmpFolderId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpMood,_tmpWeather,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDiariesByFolderId(folderId: String): Flow<List<DiaryEntity>> {
    val _sql: String = "SELECT * FROM diaries WHERE folderId = ?"
    return createFlow(__db, false, arrayOf("diaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, folderId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMood: Int = getColumnIndexOrThrow(_stmt, "mood")
        val _columnIndexOfWeather: Int = getColumnIndexOrThrow(_stmt, "weather")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<DiaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiaryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMood: String?
          if (_stmt.isNull(_columnIndexOfMood)) {
            _tmpMood = null
          } else {
            _tmpMood = _stmt.getText(_columnIndexOfMood)
          }
          val _tmpWeather: String?
          if (_stmt.isNull(_columnIndexOfWeather)) {
            _tmpWeather = null
          } else {
            _tmpWeather = _stmt.getText(_columnIndexOfWeather)
          }
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _item =
              DiaryEntity(_tmpId,_tmpFolderId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpMood,_tmpWeather,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDiariesInRootFolder(): Flow<List<DiaryEntity>> {
    val _sql: String = "SELECT * FROM diaries WHERE folderId IS NULL"
    return createFlow(__db, false, arrayOf("diaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMood: Int = getColumnIndexOrThrow(_stmt, "mood")
        val _columnIndexOfWeather: Int = getColumnIndexOrThrow(_stmt, "weather")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<DiaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiaryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMood: String?
          if (_stmt.isNull(_columnIndexOfMood)) {
            _tmpMood = null
          } else {
            _tmpMood = _stmt.getText(_columnIndexOfMood)
          }
          val _tmpWeather: String?
          if (_stmt.isNull(_columnIndexOfWeather)) {
            _tmpWeather = null
          } else {
            _tmpWeather = _stmt.getText(_columnIndexOfWeather)
          }
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _item =
              DiaryEntity(_tmpId,_tmpFolderId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpMood,_tmpWeather,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDiaryById(id: String): DiaryEntity? {
    val _sql: String = "SELECT * FROM diaries WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFolderId: Int = getColumnIndexOrThrow(_stmt, "folderId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMood: Int = getColumnIndexOrThrow(_stmt, "mood")
        val _columnIndexOfWeather: Int = getColumnIndexOrThrow(_stmt, "weather")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: DiaryEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpFolderId: String?
          if (_stmt.isNull(_columnIndexOfFolderId)) {
            _tmpFolderId = null
          } else {
            _tmpFolderId = _stmt.getText(_columnIndexOfFolderId)
          }
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMood: String?
          if (_stmt.isNull(_columnIndexOfMood)) {
            _tmpMood = null
          } else {
            _tmpMood = _stmt.getText(_columnIndexOfMood)
          }
          val _tmpWeather: String?
          if (_stmt.isNull(_columnIndexOfWeather)) {
            _tmpWeather = null
          } else {
            _tmpWeather = _stmt.getText(_columnIndexOfWeather)
          }
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _result =
              DiaryEntity(_tmpId,_tmpFolderId,_tmpTitle,_tmpContent,_tmpCreatedAt,_tmpUpdatedAt,_tmpMood,_tmpWeather,_tmpTags)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteDiary(id: String) {
    val _sql: String = "DELETE FROM diaries WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
