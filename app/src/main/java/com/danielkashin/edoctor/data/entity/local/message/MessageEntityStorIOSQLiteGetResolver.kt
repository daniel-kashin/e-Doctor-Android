package com.danielkashin.edoctor.data.entity.local.message

import android.database.Cursor
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
        val type = cursor.getInt(cursor.getColumnIndex("type"))
        val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"))
        val senderUuid = cursor.getString(cursor.getColumnIndex("sender_uuid"))
        val recipientUuid = cursor.getString(cursor.getColumnIndex("recipient_uuid"))
        val senderFullName = cursor.getString(cursor.getColumnIndex("sender_full_name"))
        val recipientFullName = cursor.getString(cursor.getColumnIndex("recipient_full_name"))
        val text = cursor.getString(cursor.getColumnIndex("text"))
        val imageRelativeUrl = cursor.getString(cursor.getColumnIndex("image_relative_url"))
        var callStatus: Int? = null
        if (!cursor.isNull(cursor.getColumnIndex("call_status"))) {
            callStatus = cursor.getInt(cursor.getColumnIndex("call_status"))
        }
        val callUuid = cursor.getString(cursor.getColumnIndex("call_uuid"))

        return MessageEntity(
            uuid,
            type,
            timestamp,
            senderUuid,
            recipientUuid,
            senderFullName,
            recipientFullName,
            text,
            imageRelativeUrl,
            callStatus,
            callUuid
        )
    }
}
