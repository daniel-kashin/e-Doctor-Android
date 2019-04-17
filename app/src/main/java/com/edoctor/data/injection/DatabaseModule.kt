package com.edoctor.data.injection

import android.content.Context
import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.local.event.MedicalEventEntitySQLiteTypeMapping
import com.edoctor.data.entity.local.message.MessageEntity
import com.edoctor.data.entity.local.message.MessageEntitySQLiteTypeMapping
import com.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.edoctor.data.entity.local.parameter.BodyParameterEntitySQLiteTypeMapping
import com.edoctor.data.local.base.DatabaseOpenHelper
import com.edoctor.data.local.event.MedicalEventLocalStore
import com.edoctor.data.local.message.MessagesLocalStore
import com.edoctor.data.local.parameter.BodyParameterLocalStore
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

    @Provides
    @Singleton
    internal fun provideMedicalEventLocalStore(
        storIOSQLite: StorIOSQLite
    ): MedicalEventLocalStore = MedicalEventLocalStore(storIOSQLite)

    @Provides
    @Singleton
    internal fun provideBodyParameterLocalStore(
        storIOSQLite: StorIOSQLite
    ): BodyParameterLocalStore = BodyParameterLocalStore(storIOSQLite)

}