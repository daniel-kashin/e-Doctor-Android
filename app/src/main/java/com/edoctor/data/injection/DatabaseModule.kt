package com.edoctor.data.injection

import android.content.Context
import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.local.event.MedicalEventEntitySQLiteTypeMapping
import com.edoctor.data.entity.local.message.MessageEntity
import com.edoctor.data.entity.local.message.MessageEntitySQLiteTypeMapping
import com.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.edoctor.data.entity.local.parameter.BodyParameterEntitySQLiteTypeMapping
import com.edoctor.data.local.base.DatabaseOpenHelper
import com.edoctor.data.local.message.MessagesLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideStorIOSQLite(
        context: Context
    ): StorIOSQLite = DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(DatabaseOpenHelper(context))
        .addTypeMapping(MessageEntity::class.java, MessageEntitySQLiteTypeMapping())
        .addTypeMapping(MedicalEventEntity::class.java, MedicalEventEntitySQLiteTypeMapping())
        .addTypeMapping(BodyParameterEntity::class.java, BodyParameterEntitySQLiteTypeMapping())
        .build()

    @Provides
    @Singleton
    internal fun provideMessageLocalStore(
        storIOSQLite: StorIOSQLite
    ): MessagesLocalStore = MessagesLocalStore(storIOSQLite)

}