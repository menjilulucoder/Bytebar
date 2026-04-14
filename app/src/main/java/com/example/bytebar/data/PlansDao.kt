package com.example.bytebar.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.bytebar.model.Plan


class PlansDao(context: Context) {
    private val dbHelper = PlansDatabaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase
    fun addPlan(plan: Plan): Boolean {
        val values = ContentValues().apply {
            put("title", plan.title)
            put("content", plan.content)
            put("start_time", plan.startTime)
            put("duration", plan.duration)
            put("is_completed", plan.isCompleted)
        }
        val result=database.insert("plan", null, values)
        return result!=-1L
    }
    fun queryPlans(): List<Plan> {
        val plans = mutableListOf<Plan>()
        val cursor = database.query("plan", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
            plans.add(Plan(id, title, content, startTime, duration, isCompleted))
        }
        cursor.close()
        return plans
    }
    fun editPlan(plan: Plan): Boolean {
        val values = ContentValues().apply {
            put("title", plan.title)
            put("content", plan.content)
            put("start_time", plan.startTime)
            put("duration", plan.duration)
            put("is_completed", if (plan.isCompleted) 1 else 0)
        }
        val result = database.update("plan", values, "id=?", arrayOf(plan.id.toString()))
        return result > 0
    }
    fun deletePlan(planId: Int): Boolean {
        val result=database.delete("plan", "id=?", arrayOf(planId.toString()))
        return result!=-1
    }
    
    fun queryPlansByDate(date: String): List<Plan> {
        val plans = mutableListOf<Plan>()
        val cursor = database.query("plan", null, "start_time LIKE ?", arrayOf("$date%"), null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
            plans.add(Plan(id, title, content, startTime, duration, isCompleted))
        }
        cursor.close()
        return plans
    }
    
    fun queryPlansByMonth(month: String): List<Plan> {
        val plans = mutableListOf<Plan>()
        val cursor = database.query("plan", null, "start_time LIKE ?", arrayOf("$month%"), null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
            plans.add(Plan(id, title, content, startTime, duration, isCompleted))
        }
        cursor.close()
        return plans
    }
}