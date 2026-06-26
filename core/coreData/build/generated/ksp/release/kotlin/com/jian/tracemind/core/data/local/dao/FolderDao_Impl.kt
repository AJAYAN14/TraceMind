package com.jian.tracemind.core.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.jian.tracemind.core.`data`.local.entity.FolderEntity
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
public class FolderDao_Impl(
  __db: RoomDatabase,
) : FolderDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFolderEntity: EntityInsertAdapter<FolderEntity>

  private val __updateAdapterOfFolderEntity: EntityDeleteOrUpdateAdapter<FolderEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFolderEntity = object : EntityInsertAdapter<FolderEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `folders` (`id`,`parentId`,`name`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FolderEntity) {
        statement.bindText(1, entity.id)
        val _tmpParentId: String? = entity.parentId
        if (_tmpParentId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpParentId)
        }
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.createdAt)
        statement.bindLong(5, entity.updatedAt)
      }
    }
    this.__updateAdapterOfFolderEntity = object : EntityDeleteOrUpdateAdapter<FolderEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `folders` SET `id` = ?,`parentId` = ?,`name` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FolderEntity) {
        statement.bindText(1, entity.id)
        val _tmpParentId: String? = entity.parentId
        if (_tmpParentId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpParentId)
        }
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.createdAt)
        statement.bindLong(5, entity.updatedAt)
        statement.bindText(6, entity.id)
      }
    }
  }

  public override suspend fun insertFolder(folder: FolderEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFolderEntity.insert(_connection, folder)
  }

  public override suspend fun updateFolder(folder: FolderEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfFolderEntity.handle(_connection, folder)
  }

  public override fun getAllFolders(): Flow<List<FolderEntity>> {
    val _sql: String = "SELECT * FROM folders"
    return createFlow(__db, false, arrayOf("folders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FolderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FolderEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpParentId: String?
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null
          } else {
            _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = FolderEntity(_tmpId,_tmpParentId,_tmpName,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFolderById(id: String): FolderEntity? {
    val _sql: String = "SELECT * FROM folders WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: FolderEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpParentId: String?
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null
          } else {
            _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = FolderEntity(_tmpId,_tmpParentId,_tmpName,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteFolder(id: String) {
    val _sql: String = "DELETE FROM folders WHERE id = ?"
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
