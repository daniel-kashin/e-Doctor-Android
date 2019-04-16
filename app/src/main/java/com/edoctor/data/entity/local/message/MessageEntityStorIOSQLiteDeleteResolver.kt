package com.edoctor.data.entity.local.message

import com.edoctor.data.entity.local.MessageEntity
import com.pushtorefresh.storio3.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery

/**
 * Generated resolver for Delete Operation.
 */
class MessageEntityStorIOSQLiteDeleteResolver : DefaultDeleteResolver<MessageEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToDeleteQuery(`object`: MessageEntity): DeleteQuery {
        return DeleteQuery.builder()
            .table("messages")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }

}
