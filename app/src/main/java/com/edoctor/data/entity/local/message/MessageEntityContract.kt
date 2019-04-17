package com.edoctor.data.entity.local.message

object MessageEntityContract {
    const val TABLE_NAME = "messages"

    const val COLUMN_UUID = "uuid"
    const val COLUMN_TIMESTAMP = "timestamp"
    const val COLUMN_TEXT = "text"
    const val COLUMN_TYPE = "type"
    const val COLUMN_IMAGE_RELATIVE_URL = "image_relative_url"
    const val COLUMN_CALL_STATUS = "call_status"
    const val COLUMN_CALL_UUID = "call_uuid"
    const val COLUMN_SENDER_UUID = "sender_uuid"
    const val COLUMN_RECIPIENT_UUID = "recipient_uuid"
    const val COLUMN_SENDER_FULL_NAME = "sender_full_name"
    const val COLUMN_RECIPIENT_FULL_NAME = "recipient_full_name"

    const val CREATE_TABLE_QUERY = """
        CREATE TABLE $TABLE_NAME(
            $COLUMN_UUID TEXT NOT NULL PRIMARY KEY,
            $COLUMN_TIMESTAMP INTEGER NOT NULL,
            $COLUMN_SENDER_UUID TEXT NOT NULL,
            $COLUMN_RECIPIENT_UUID TEXT NOT NULL,
            $COLUMN_TEXT TEXT,
            $COLUMN_TYPE INTEGER,
            $COLUMN_IMAGE_RELATIVE_URL TEXT,
            $COLUMN_CALL_STATUS INTEGER,
            $COLUMN_CALL_UUID TEXT,
            $COLUMN_SENDER_FULL_NAME TEXT,
            $COLUMN_RECIPIENT_FULL_NAME TEXT
    );"""

    internal val DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
}