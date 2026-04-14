package com.example.bytebar.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bytebar.NoteDetailActivity
import com.example.bytebar.databinding.DialogNoteBinding
import com.example.bytebar.databinding.FragmentNotesBinding
import com.example.bytebar.model.Note
import com.example.bytebar.ui.notes.NotesContract
import com.example.bytebar.ui.notes.NotesPresenter
import com.example.bytebar.ui.notes.adapters.NotesAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 心得沉淀页面Fragment
 */
class NotesFragment : Fragment(), NotesContract.View {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var presenter: NotesContract.Presenter
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        // 初始化Presenter
        presenter = NotesPresenter(this, requireContext())
        // 初始化RecyclerView
        setupRecyclerView()
        // 加载心得数据
        presenter.loadNotes()
        // 设置新建按钮点击事件
        binding.addNoteButton.setOnClickListener {
            presenter.addNote()
        }
        // 设置删除按钮点击事件
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmDialog()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter()
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecyclerView.adapter = adapter
        
        // 设置笔记点击事件
        adapter.setOnNoteClickListener(object : NotesAdapter.OnNoteClickListener {
            override fun onDetailClick(note: Note) {
                // 跳转到详情页面
                val intent = Intent(requireContext(), NoteDetailActivity::class.java)
                intent.putExtra("note_id", note.id)
                startActivity(intent)
            }
            
            override fun onLongClick(note: Note) {
                // 进入选择模式
                adapter.enterSelectionMode()
                adapter.toggleNoteSelection(note.id)
                binding.deleteLayout.visibility = View.VISIBLE
            }
            
            override fun onSelectionChanged(selectedCount: Int) {
                if (selectedCount == 0) {
                    // 退出选择模式
                    adapter.exitSelectionMode()
                    binding.deleteLayout.visibility = View.GONE
                }
            }
        })
    }

    override fun showNotes(notes: List<Note>) {
        adapter.setNotes(notes)
    }

    override fun showAddNoteDialog() {
        val dialogBinding = DialogNoteBinding.inflate(layoutInflater)
        
        // 创建日历实例用于日期选择
        val calendar = Calendar.getInstance()
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("新建心得")
            .setView(dialogBinding.root)
            .setPositiveButton("保存") { _, _ ->
                val title = dialogBinding.noteTitleInput.text.toString().trim()
                val content = dialogBinding.noteContentInput.text.toString().trim()
                val date = dialogBinding.noteTimeInput.text.toString().trim()
                
                if (title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                    val note = Note(0, title, content, date)
                    presenter.saveNote(note)
                } else {
                    showError("请填写完整信息")
                }
            }
            .setNegativeButton("取消", null)
            .create()
        
        // 为时间输入框添加点击事件，弹出日期选择器
        dialogBinding.noteTimeInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dialogBinding.noteTimeInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        dialog.show()
    }

    override fun showEditNoteDialog(note: Note) {
        val dialogBinding = DialogNoteBinding.inflate(layoutInflater)
        dialogBinding.noteTitleInput.setText(note.title)
        dialogBinding.noteContentInput.setText(note.content)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("编辑心得")
            .setView(dialogBinding.root)
            .setPositiveButton("保存") { _, _ ->
                val title = dialogBinding.noteTitleInput.text.toString().trim()
                val content = dialogBinding.noteContentInput.text.toString().trim()
                
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val updatedNote = note.copy(title = title, content = content)
                    presenter.updateNote(updatedNote)
                }
            }
            .setNegativeButton("取消", null)
            .create()
        
        dialog.show()
    }

    override fun showDeleteConfirmDialog(noteId: Int) {
        // 单个删除功能暂时不需要
    }

    /**
     * 显示批量删除确认对话框
     */
    private fun showDeleteConfirmDialog() {
        val selectedIds = adapter.getSelectedNoteIds()
        if (selectedIds.isEmpty()) return
        
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除选中的心得吗？")
            .setPositiveButton("删除") { _, _ ->
                // 批量删除
                for (noteId in selectedIds) {
                    presenter.removeNote(noteId)
                }
                // 退出选择模式
                adapter.exitSelectionMode()
                binding.deleteLayout.visibility = View.GONE
            }
            .setNegativeButton("取消", null)
            .create()
            .show()
    }

    override fun showLoading() {
        binding.loadingProgress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingProgress.visibility = View.GONE
    }

    override fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("错误")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .create()
            .show()
    }
}