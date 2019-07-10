package com.danielkashin.edoctor.data.local.message

import com.danielkashin.edoctor.data.entity.local.message.MessageEntity
import com.danielkashin.edoctor.data.entity.local.message.MessageEntityContract
import com.danielkashin.edoctor.data.local.base.BaseLocalStore
import com.danielkashin.edoctor.data.entity.local.message.MessageEntityContract.COLUMN_RECIPIENT_UUID
import com.danielkashin.edoctor.data.entity.local.message.MessageEntityContract.COLUMN_SENDER_UUID
import com.danielkashin.edoctor.data.entity.local.message.MessageEntityContract.COLUMN_TIMESTAMP
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Single

class MessagesLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<MessageEntity>(storIOSQLite) {

    override val objectClass = MessageEntity::class.java
    override val tableName = MessageEntityContract.TABLE_NAME
    override val idColumnName = MessageEntityContract.COLUMN_UUID

    private fun getConversationMessageQuery(fromTimestamp: Long, userUuid: String, recipientUuid: String) =
        Query.builder()
            .table(tableName)
            .where(
                "$COLUMN_TIMESTAMP > ? " +
                        "AND ($COLUMN_SENDER_UUID = ? OR $COLUMN_RECIPIENT_UUID = ?) " +
                        "AND ($COLUMN_SENDER_UUID = ? OR $COLUMN_RECIPIENT_UUID = ?)"
            )
            .whereArgs(fromTimestamp, userUuid, userUuid, recipientUuid, recipientUuid)
            .build()

    fun getConversationMessages(
        fromTimestamp: Long,
        userUuid: String,
        recipientUuid: String
    ): Single<List<MessageEntity>> = getAllByQuery(getConversationMessageQuery(fromTimestamp, userUuid, recipientUuid))

    fun getConversationMessagesBlocking(
        fromTimestamp: Long,
        userUuid: String,
        recipientUuid: String
    ): List<MessageEntity> = getAllByQueryBlocking(getConversationMessageQuery(fromTimestamp, userUuid, recipientUuid))

    fun getConversations(userUuid: String): Single<List<MessageEntity>> =
        getAllByQuery(
            Query.builder()
                .table(tableName)
                .groupBy("$COLUMN_SENDER_UUID, $COLUMN_RECIPIENT_UUID")
                .where("$COLUMN_SENDER_UUID = ? OR $COLUMN_RECIPIENT_UUID = ?")
                .whereArgs(userUuid, userUuid)
                .build()
        ).map { conversationList ->
            conversationList
                .distinctBy { it.senderUuid.takeIf { it != userUuid } ?: it.recipientUuid }
                .mapNotNull { conversation ->
                    val recipientUuid = conversation.senderUuid.takeIf { it != userUuid } ?: conversation.recipientUuid
                    getConversationMessagesBlocking(-1, userUuid, recipientUuid).maxBy { it.timestamp }
                }
        }

}