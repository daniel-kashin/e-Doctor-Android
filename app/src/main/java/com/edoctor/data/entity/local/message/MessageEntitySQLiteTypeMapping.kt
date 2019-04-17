package com.edoctor.data.entity.local.message

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping

/**
 * Generated mapping with collection of resolvers.
 */
class MessageEntitySQLiteTypeMapping : SQLiteTypeMapping<MessageEntity>(
    MessageEntityStorIOSQLitePutResolver(),
    MessageEntityStorIOSQLiteGetResolver(),
    MessageEntityStorIOSQLiteDeleteResolver()
)
