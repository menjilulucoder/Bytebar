package com.example.bytebar.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class UserDatabaseHelper(context: Context): SQLiteOpenHelper(context, "bytebar.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE IF NOT EXISTS user (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                password TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createUserTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 升级数据库时执行
    }
}
