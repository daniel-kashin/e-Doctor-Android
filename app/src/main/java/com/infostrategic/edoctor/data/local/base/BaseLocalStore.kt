package com.infostrategic.edoctor.data.local.base

import android.database.Cursor
import android.util.Log.v
import com.pushtorefresh.storio3.Optional
import com.pushtorefresh.storio3.Queries
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio3.sqlite.queries.Query
import com.pushtorefresh.storio3.sqlite.queries.RawQuery
import io.reactivex.*

abstract class BaseLocalStore<T>(protected val storIOSQLite: StorIOSQLite) {

    protected val logTag by lazy { "BaseLocalStore($objectClass}" }

    protected abstract val objectClass: Class<T>
    protected abstract val tableName: String
    protected abstract val idColumnName: String

    fun getById(id: String): Single<Optional<T>> {
        return getOneByQuery(
            Query.builder()
            .table(tableName)
            .where("$idColumnName = ?")
            .whereArgs(id)
            .build()
        )
    }

    fun getCursorBlocking(rawQuery: RawQuery): Cursor? {
        return storIOSQLite.get()
            .cursor()
            .withQuery(rawQuery)
            .prepare()
            .executeAsBlocking()
    }

    val all: Single<List<T>>
        get() = getAllByQuery(Query.builder()
            .table(tableName)
            .build())

    fun save(t: T): Single<T> {
        return storIOSQLite.put()
            .`object`(t)
            .prepare()
            .asRxSingle()
            .doOnSubscribe { v(logTag, "save(): $t") }
            .map { t }
    }

    fun saveBlocking(t: T) {
        v(logTag, "saveBlocking(): $t")
        storIOSQLite.put()
            .`object`(t)
            .prepare()
            .executeAsBlocking()
    }

    fun save(list: List<T>): Single<List<T>> {
        return storIOSQLite.put()
            .objects(list)
            .prepare()
            .asRxSingle()
            .doOnSubscribe { v(logTag, "save(): $list") }
            .map { list }
    }

    fun saveBlocking(list: List<T>) {
        v(logTag, "saveBlocking(): $list")
        storIOSQLite.put()
            .objects(list)
            .prepare()
            .executeAsBlocking()
    }

    fun deleteById(id: String) {
        v(logTag, "deleteById(): $id")
        deleteByQuery(
            DeleteQuery.builder()
            .table(tableName)
            .where("$idColumnName = ?")
            .whereArgs(id)
            .build())
    }

    fun deleteByIds(ids: List<String>) {
        v(logTag, "deleteByIds(): $ids")
        deleteByQuery(DeleteQuery.builder()
            .table(tableName)
            .where("$idColumnName IN (${Queries.placeholders(ids.size)})")
            .whereArgs(*ids.toTypedArray())
            .build())
    }

    protected fun getOneByQuery(query: Query): Single<Optional<T>> {
        return storIOSQLite.get()
            .`object`(objectClass)
            .withQuery(query)
            .prepare()
            .asRxSingle()
    }

    fun getAllByQuery(query: Query): Single<List<T>> {
        return storIOSQLite.get()
            .listOfObjects(objectClass)
            .withQuery(query)
            .prepare()
            .asRxSingle()
    }

    fun getAllByQueryBlocking(query: Query): List<T> {
        return storIOSQLite.get()
            .listOfObjects(objectClass)
            .withQuery(query)
            .prepare()
            .executeAsBlocking()
            .orEmpty()
    }

    protected fun observeForQuery(query: Query): Flowable<List<T>> {
        return storIOSQLite.get()
            .listOfObjects(objectClass)
            .withQuery(query)
            .prepare()
            .asRxFlowable(BackpressureStrategy.BUFFER)
    }

    protected fun observeCursorForQuery(rawQuery: RawQuery): Flowable<Cursor> {
        return storIOSQLite.get()
            .cursor()
            .withQuery(rawQuery)
            .prepare()
            .asRxFlowable(BackpressureStrategy.BUFFER)
    }

    protected fun getCursorForQuery(rawQuery: RawQuery): Single<Cursor> {
        return storIOSQLite.get()
            .cursor()
            .withQuery(rawQuery)
            .prepare()
            .asRxSingle()
    }

    protected fun deleteByQuery(deleteQuery: DeleteQuery) {
        storIOSQLite.delete()
            .byQuery(deleteQuery)
            .prepare()
            .executeAsBlocking()
    }
}
