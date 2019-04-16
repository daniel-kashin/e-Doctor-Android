package com.edoctor.data.entity.local.message

import android.database.Cursor
import com.edoctor.data.entity.local.MessageEntity
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.operations.get.DefaultGetResolver

/**
 * Generated resolver for Get Operation.
 */
class MessageEntityStorIOSQLiteGetResolver : DefaultGetResolver<MessageEntity>() {
    /**
     * {@inheritDoc}
     */
    override fun mapFromCursor(storIOSQLite: StorIOSQLite, cursor: Cursor): MessageEntity {

        val uuid = cursor.getString(cursor.getColumnIndex("uuid"))
        val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"))
        val text = cursor.getString(cursor.getColumnIndex("text"))
        var type: Int? = null
        if (!cursor.isNull(cursor.getColumnIndex("type"))) {
            type = cursor.getInt(cursor.getColumnIndex("type"))
        }
        val imageRelativeUrl = cursor.getString(cursor.getColumnIndex("image_relative_url"))
        var callStatus: Int? = null
        if (!cursor.isNull(cursor.getColumnIndex("call_status"))) {
            callStatus = cursor.getInt(cursor.getColumnIndex("call_status"))
        }
        val callUuid = cursor.getString(cursor.getColumnIndex("call_uuid"))
        val senderUuid = cursor.getString(cursor.getColumnIndex("sender_uuid"))
        val recipientUuid = cursor.getString(cursor.getColumnIndex("recipient_uuid"))

        return MessageEntity(
            uuid,
            timestamp,
            text,
            type,
            imageRelativeUrl,
            callStatus,
            callUuid,
            senderUuid,
            recipientUuid
        )
    }
}
