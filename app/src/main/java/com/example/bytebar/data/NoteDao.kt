package com.example.bytebar.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.bytebar.model.Note

class NoteDao(context: Context) {
    private val dbHelper = NoteDatabaseHelper(context)
    
    fun addNote(note: Note): Boolean {
        val database: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("date", note.date)
        }
        val result = database.insert("notes", null, values)
        database.close()
        return result != -1L
    }
    
    fun editNote(note: Note): Boolean {
        val database: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("date", note.date)
        }
        val result = database.update("notes", values, "id = ?", arrayOf(note.id.toString()))
        database.close()
        return result != -1
    }
    
    fun deleteNote(noteId: Int): Boolean {
        val database: SQLiteDatabase = dbHelper.writableDatabase
        val result = database.delete("notes", "id = ?", arrayOf(noteId.toString()))
        database.close()
        return result != 0
    }
    
    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val database: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = database.query("notes", null, null, null, null, null, "date DESC")
        
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                notes.add(Note(id, title, content, date))
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        database.close()
        return notes
    }
}