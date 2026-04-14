package com.example.bytebar.ui.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bytebar.databinding.ItemNoteBinding
import com.example.bytebar.model.Note

/**
 * 心得列表适配器
 */
class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private var notes: List<Note> = mutableListOf()
    private var isSelectionMode = false
    private val selectedNotes = mutableSetOf<Int>()
    private var onNoteClickListener: OnNoteClickListener? = null

    /**
     * 笔记点击事件监听器接口
     */
    interface OnNoteClickListener {
        fun onDetailClick(note: Note)
        fun onLongClick(note: Note)
        fun onSelectionChanged(selectedCount: Int)
    }

    /**
     * 设置点击事件监听器
     */
    fun setOnNoteClickListener(listener: OnNoteClickListener) {
        this.onNoteClickListener = listener
    }

    /**
     * 设置心得数据
     */
    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    /**
     * 进入选择模式
     */
    fun enterSelectionMode() {
        isSelectionMode = true
        notifyDataSetChanged()
    }

    /**
     * 退出选择模式
     */
    fun exitSelectionMode() {
        isSelectionMode = false
        selectedNotes.clear()
        notifyDataSetChanged()
        onNoteClickListener?.onSelectionChanged(0)
    }

    /**
     * 切换笔记选择状态
     */
    fun toggleNoteSelection(noteId: Int) {
        if (selectedNotes.contains(noteId)) {
            selectedNotes.remove(noteId)
        } else {
            selectedNotes.add(noteId)
        }
        notifyDataSetChanged()
        onNoteClickListener?.onSelectionChanged(selectedNotes.size)
    }

    /**
     * 获取选中的笔记ID列表
     */
    fun getSelectedNoteIds(): List<Int> {
        return selectedNotes.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size

    /**
     * 心得列表项ViewHolder
     */
    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.noteTitle.text = note.title
            binding.noteDate.text = note.date

            // 根据选择模式显示或隐藏复选框
            binding.noteCheckbox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            binding.noteCheckbox.isChecked = selectedNotes.contains(note.id)

            // 设置详情按钮点击事件
            binding.noteDetailButton.setOnClickListener {
                if (!isSelectionMode) {
                    onNoteClickListener?.onDetailClick(note)
                }
            }

            // 设置根布局点击事件
            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    toggleNoteSelection(note.id)
                }
            }

            // 设置根布局长按事件
            binding.root.setOnLongClickListener {
                if (!isSelectionMode) {
                    onNoteClickListener?.onLongClick(note)
                    true
                } else {
                    false
                }
            }
        }
    }
}