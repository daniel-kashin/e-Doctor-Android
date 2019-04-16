package com.edoctor.data.entity.local

import com.edoctor.data.local.message.MessageEntityContract
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteCreator
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType

@StorIOSQLiteType(table = MessageEntityContract.TABLE_NAME)
data class MessageEntity @StorIOSQLiteCreator constructor(

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_UUID, key = true)
    val uuid: String,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_TIMESTAMP)
    val timestamp: Long,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_TEXT)
    val text: String? = null,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_TYPE)
    val type: Int? = null,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_IMAGE_RELATIVE_URL)
    val imageRelativeUrl: String? = null,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_CALL_STATUS)
    val callStatus: Int? = null,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_CALL_UUID)
    val callUuid: String? = null,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_SENDER_UUID)
    val senderUuid: String,

    @StorIOSQLiteColumn(name = MessageEntityContract.COLUMN_RECIPIENT_UUID)
    val recipientUuid: String

)