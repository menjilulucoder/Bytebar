package com.example.bytebar.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NoteDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    "notes.db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // 创建数据库表
        val createTableSql = """
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                date TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableSql)
    }
    
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 升级数据库时执行
    }

}