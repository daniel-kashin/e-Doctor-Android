package com.edoctor.data.entity.local.message

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping

class MessageEntitySQLiteTypeMapping : SQLiteTypeMapping<MessageEntity>(
    MessageEntityStorIOSQLitePutResolver(),
    MessageEntityStorIOSQLiteGetResolver(),
    MessageEntityStorIOSQLiteDeleteResolver()
)