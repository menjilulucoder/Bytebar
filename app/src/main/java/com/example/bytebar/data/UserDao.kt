package com.example.bytebar.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

/**
 * 用户数据访问对象 (User Data Access Object)
 * 负责处理与用户表相关的所有数据库操作（增、删、改、查）
 */
class UserDao(context: Context) {

    // 数据库帮助类实例
    private val dbHelper = UserDatabaseHelper(context)
    // 获取可写的数据库连接
    private val database: SQLiteDatabase = dbHelper.writableDatabase

    /**
     * 用户注册
     * 将新用户的信息插入到数据库表中
     *
     * @param username 用户名
     * @param password 密码
     * @return Boolean - 插入成功返回 true，失败返回 false
     */
    fun register(username: String, password: String): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        // insert 方法返回新行的行号，如果插入失败则返回 -1
        val result = database.insert("user", null, values)
        return result != -1L
    }

    /**
     * 用户登录
     * 根据用户名和密码查询数据库，验证用户身份
     *
     * @param username 用户名
     * @param password 密码
     * @return Boolean - 如果找到匹配的用户返回 true，否则返回 false
     */
    fun login(username: String, password: String): Boolean {
        // 执行查询：SELECT * FROM user WHERE username=? AND password=?
        val cursor = database.query(
            "user",
            null,
            "username=? and password=?",
            arrayOf(username, password),
            null, null, null
        )

        // 【关键修复】先获取查询结果的数量
        val count = cursor.count

        // 使用完毕后关闭 Cursor 释放资源
        cursor.close()

        // 如果数量大于 0，说明用户存在且密码正确
        return count > 0
    }

    /**
     * 检查用户是否存在
     * 仅根据用户名查询数据库
     *
     * @param username 用户名
     * @return Boolean - 用户存在返回 true，不存在返回 false
     */
    fun isUserExist(username: String): Boolean {
        // 执行查询：SELECT * FROM user WHERE username=?
        val cursor = database.query("user", null, "username=?", arrayOf(username), null, null, null)

        // 先获取数量
        val count = cursor.count
        // 关闭 Cursor
        cursor.close()

        return count > 0
    }

    /**
     * 查找用户 ID
     * 根据用户名获取用户的唯一标识符 (ID)
     *
     * @param username 用户名
     * @return Int - 返回用户的 ID，如果用户不存在则返回 -1
     */
    fun findUserId(username: String): Int {
        // 执行查询
        val cursor = database.query("user", null, "username=?", arrayOf(username), null, null, null)

        var userId = -1 // 默认返回 -1，表示未找到用户

        // 如果游标能移动到第一行，说明查询到了数据
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow("id")
            userId = cursor.getInt(idIndex)
        }

        // 关闭资源
        cursor.close()
        return userId
    }

    /**
     * 关闭数据库连接
     * 在应用退出或不再需要数据库操作时调用
     */
    fun close() {
        dbHelper.close()
    }
}//未完成