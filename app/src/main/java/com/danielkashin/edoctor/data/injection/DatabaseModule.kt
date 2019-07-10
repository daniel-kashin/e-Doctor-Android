package com.danielkashin.edoctor.data.injection

import android.content.Context
import com.danielkashin.edoctor.data.entity.local.event.MedicalEventEntity
import com.danielkashin.edoctor.data.entity.local.event.MedicalEventEntitySQLiteTypeMapping
import com.danielkashin.edoctor.data.entity.local.message.MessageEntity
import com.danielkashin.edoctor.data.entity.local.message.MessageEntitySQLiteTypeMapping
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntitySQLiteTypeMapping
import com.danielkashin.edoctor.data.local.base.DatabaseOpenHelper
import com.danielkashin.edoctor.data.local.event.MedicalEventLocalStore
import com.danielkashin.edoctor.data.local.message.MessagesLocalStore
import com.danielkashin.edoctor.data.local.parameter.BodyParameterLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabaseOpenHelper(context: Context): DatabaseOpenHelper = DatabaseOpenHelper(context)

    @Provides
    @Singleton
    internal fun provideStorIOSQLite(
        databaseOpenHelper: DatabaseOpenHelper
    ): StorIOSQLite = DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(databaseOpenHelper)
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