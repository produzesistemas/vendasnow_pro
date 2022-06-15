package com.produze.sistemas.vendasnow.vendasnowpremium.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import java.lang.Exception

class DataSourceUser constructor(context: Context){
    private val dbHelper = SqliteHelper(context)
    private var database: SQLiteDatabase? = null
    private lateinit var token: Token

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    private fun close() {
        dbHelper.close()
    }

    fun insert(user: Token)
    {
        try {
            open()
            val values = ContentValues().apply {
                put("email", user.email)
                put("token", user.token)
                put("userName", user.userName)
                put("role", user.role)
            }
            database?.insert("User", null, values)
        }
            catch (e: Exception) {
            } finally {
                close()
            }
    }

    fun deleteAll() {
        try {
            open()
            database!!.delete("User", "", null)
        } catch (e: Exception) {
        } finally {
            database!!.close()
        }
    }

    fun get(): Token {
        token = Token()
        try {
            open()
            val cursor = database?.rawQuery("select * from User", null)
            with(cursor) {
                while (this?.moveToNext()!!) {
                    token.email = getString(getColumnIndexOrThrow("email"))
                    token.userName = getString(getColumnIndexOrThrow("userName"))
                    token.token = getString(getColumnIndexOrThrow("token"))
                    token.role = getString(getColumnIndexOrThrow("role"))
                }
            }

        } catch (e: Exception) {
        } finally {
            database!!.close()
        }
        return token
    }


}