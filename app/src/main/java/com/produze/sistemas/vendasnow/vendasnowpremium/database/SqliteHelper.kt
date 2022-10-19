package com.produze.sistemas.vendasnow.vendasnowpremium.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val createTableUser = "CREATE TABLE User (" +
            "token TEXT," +
            " email TEXT," +
            " userName TEXT," +
            " role TEXT)"

    private val deleteTableUser = "DROP TABLE IF EXISTS User"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableUser)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(deleteTableUser)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "vendasnow.db"
    }
}