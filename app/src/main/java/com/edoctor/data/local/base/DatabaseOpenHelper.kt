package com.edoctor.data.local.base

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.edoctor.data.entity.local.event.MedicalEventEntityContract
import com.edoctor.data.entity.local.message.MessageEntityContract
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "EDOCTOR_DATABASE"
        private val DATABASE_VERSION = 6
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MessageEntityContract.CREATE_TABLE_QUERY)
        db.execSQL(MedicalEventEntityContract.CREATE_TABLE_QUERY)
        db.execSQL(BodyParameterEntityContract.CREATE_TABLE_QUERY)
    }

    private fun onDelete(db: SQLiteDatabase) {
        db.execSQL(MessageEntityContract.DELETE_TABLE_QUERY)
        db.execSQL(MedicalEventEntityContract.DELETE_TABLE_QUERY)
        db.execSQL(BodyParameterEntityContract.DELETE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
       recreateTables(db)
    }

    fun recreateTables() = recreateTables(writableDatabase)

    private fun recreateTables(db: SQLiteDatabase) {
        onDelete(db)
        onCreate(db)
    }

}