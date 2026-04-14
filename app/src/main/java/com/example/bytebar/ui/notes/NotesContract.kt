package com.example.bytebar.ui.notes

import com.example.bytebar.model.Note
/**
 * 心得沉淀模块的MVP契约接口
 */
interface NotesContract {
    /**
     * View接口，定义UI操作
     */
    interface View {
        /**
         * 显示心得列表
         */
        fun showNotes(notes: List<Note>)

        /**
         * 显示新建心得对话框
         */
        fun showAddNoteDialog()

        /**
         * 显示编辑心得对话框
         */
        fun showEditNoteDialog(note: Note)

        /**
         * 显示删除确认对话框
         */
        fun showDeleteConfirmDialog(noteId: Int)

        /**
         * 显示加载状态
         */
        fun showLoading()

        /**
         * 隐藏加载状态
         */
        fun hideLoading()

        /**
         * 显示错误信息
         */
        fun showError(message: String)
    }

    /**
     * Presenter接口，定义业务逻辑
     */
    interface Presenter {
        /**
         * 加载心得列表
         */
        fun loadNotes()

        /**
         * 添加心得
         */
        fun addNote()

        /**
         * 编辑心得
         */
        fun editNote(note: Note)

        /**
         * 删除心得
         */
        fun deleteNote(noteId: Int)

        /**
         * 保存心得
         */
        fun saveNote(note: Note)

        /**
         * 更新心得
         */
        fun updateNote(note: Note)

        /**
         * 移除心得
         */
        fun removeNote(noteId: Int)

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
}