package com.edoctor.data.entity.local.message

import android.content.ContentValues
import com.edoctor.data.entity.local.MessageEntity
import com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery

/**
 * Generated resolver for Put Operation.
 */
class MessageEntityStorIOSQLitePutResolver : DefaultPutResolver<MessageEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToInsertQuery(`object`: MessageEntity): InsertQuery {
        return InsertQuery.builder()
            .table("messages")
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToUpdateQuery(`object`: MessageEntity): UpdateQuery {
        return UpdateQuery.builder()
            .table("messages")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToContentValues(`object`: MessageEntity): ContentValues {
        val contentValues = ContentValues(9)

        contentValues.put("uuid", `object`.uuid)
        contentValues.put("timestamp", `object`.timestamp)
        contentValues.put("text", `object`.text)
        contentValues.put("type", `object`.type)
        contentValues.put("image_relative_url", `object`.imageRelativeUrl)
        contentValues.put("call_status", `object`.callStatus)
        contentValues.put("call_uuid", `object`.callUuid)
        contentValues.put("sender_uuid", `object`.senderUuid)
        contentValues.put("recipient_uuid", `object`.recipientUuid)

        return contentValues
    }
}
