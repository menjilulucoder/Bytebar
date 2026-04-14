package com.example.bytebar.data
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlansDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "bytebar_plan.db", null, 1) {
override fun onCreate(db: SQLiteDatabase?) {
    val createPlanTable = """
        CREATE TABLE IF NOT EXISTS `plan` (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            content TEXT NOT NULL,
            start_time TEXT NOT NULL,
            duration INTEGER NOT NULL,
            is_completed INTEGER NOT NULL
        )
    """.trimIndent()
    db?.execSQL(createPlanTable)
}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 升级数据库时执行
    }
}