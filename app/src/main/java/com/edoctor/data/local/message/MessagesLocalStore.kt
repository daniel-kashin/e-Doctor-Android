package com.edoctor.data.local.message

import com.edoctor.data.entity.local.MessageEntity
import com.edoctor.data.local.base.BaseLocalStore
import com.edoctor.data.local.message.MessageEntityContract.COLUMN_TIMESTAMP
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Single

class MessagesLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<MessageEntity>(storIOSQLite) {

    override val objectClass = MessageEntity::class.java
    override val tableName = MessageEntityContract.TABLE_NAME
    override val idColumnName = MessageEntityContract.COLUMN_UUID

    fun getMessages(fromTimestamp: Long): Single<List<MessageEntity>> =
        getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("$COLUMN_TIMESTAMP > ?")
                .whereArgs(fromTimestamp)
                .build()
        )

}