package com.example.bytebar.ui.notes

import android.content.Context
import com.example.bytebar.data.NoteDao
import com.example.bytebar.model.Note

/**
 * 心得沉淀模块的Presenter，实现业务逻辑
 */
class NotesPresenter(private val view: NotesContract.View, private val context: Context) : NotesContract.Presenter {

    private val noteDao = NoteDao(context)

    override fun loadNotes() {
        // 显示加载状态
        view.showLoading()

        // 从数据库加载数据
        val notes = noteDao.getAllNotes()

        // 显示心得列表
        view.showNotes(notes)

        // 隐藏加载状态
        view.hideLoading()
    }

    override fun addNote() {
        view.showAddNoteDialog()
    }

    override fun editNote(note: Note) {
        view.showEditNoteDialog(note)
    }

    override fun deleteNote(noteId: Int) {
        view.showDeleteConfirmDialog(noteId)
    }

    override fun saveNote(note: Note) {
        val success = noteDao.addNote(note)
        if (success) {
            // 重新加载笔记列表
            loadNotes()
        } else {
            view.showError("保存失败")
        }
    }

    override fun updateNote(note: Note) {
        val success = noteDao.editNote(note)
        if (success) {
            // 重新加载笔记列表
            loadNotes()
        } else {
            view.showError("更新失败")
        }
    }

    override fun removeNote(noteId: Int) {
        val success = noteDao.deleteNote(noteId)
        if (success) {
            // 重新加载笔记列表
            loadNotes()
        } else {
            view.showError("删除失败")
        }
    }

    override fun destroy() {
        // 清理资源
    }
}