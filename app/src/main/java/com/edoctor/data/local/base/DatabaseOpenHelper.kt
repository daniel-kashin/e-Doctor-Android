package com.edoctor.data.local.base

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.edoctor.data.entity.local.message.MessageEntityContract

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "EDOCTOR_DATABASE"
        private val DATABASE_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MessageEntityContract.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDelete(db)
        onCreate(db)
    }

    private fun onDelete(db: SQLiteDatabase) {
        db.execSQL(MessageEntityContract.DELETE_TABLE_QUERY)
    }

}