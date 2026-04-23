package com.example.bytebar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebar.ai.SparkApi
import com.example.bytebar.data.NoteDao
import com.example.bytebar.databinding.ActivityNoteDetailBinding
import com.example.bytebar.databinding.DialogNoteBinding
import com.example.bytebar.model.Note
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteDetailBinding
    private lateinit var noteDao: NoteDao
    private var currentNote: Note? = null
    private var isPolishing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteDao = NoteDao(this)

        // 获取笔记ID
        val noteId = intent.getIntExtra("note_id", -1)
        if (noteId != -1) {
            // 加载并显示笔记详情
            loadNoteDetail(noteId)
        }

        // 设置编辑按钮点击事件
        binding.editButton.setOnClickListener {
            currentNote?.let { note ->
                showEditDialog(note)
            }
        }

        // 设置Polish按钮点击事件
        binding.polishButton.setOnClickListener {
            currentNote?.let { note ->
                polishNote(note)
            }
        }
    }

    private fun loadNoteDetail(noteId: Int) {
        // 从数据库获取笔记
        val notes = noteDao.getAllNotes()
        val note = notes.find { it.id == noteId }

        if (note != null) {
            currentNote = note
            binding.noteTitle.text = note.title
            binding.noteContent.text = note.content
            binding.noteDate.text = note.date
        }
    }

    private fun showEditDialog(note: Note) {
        val dialogBinding = DialogNoteBinding.inflate(layoutInflater)
        
        // 填充现有数据
        dialogBinding.noteTitleInput.setText(note.title)
        dialogBinding.noteContentInput.setText(note.content)
        dialogBinding.noteTimeInput.setText(note.date)
        
        val calendar = Calendar.getInstance()
        
        // 为时间输入框添加点击事件，弹出日期选择器
        dialogBinding.noteTimeInput.setOnClickListener {
            DatePickerDialog(
                this,
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

        
        val dialog = AlertDialog.Builder(this)
            .setTitle("编辑心得")
            .setView(dialogBinding.root)
            .setPositiveButton("保存") { _, _ ->
                val title = dialogBinding.noteTitleInput.text.toString().trim()
                val content = dialogBinding.noteContentInput.text.toString().trim()
                val date = dialogBinding.noteTimeInput.text.toString().trim()
                
                if (title.isNotEmpty() && content.isNotEmpty() && date.isNotEmpty()) {
                    val updatedNote = note.copy(title = title, content = content, date = date)
                    val success = noteDao.editNote(updatedNote)
                    if (success) {
                        // 更新UI
                        binding.noteTitle.text = title
                        binding.noteContent.text = content
                        binding.noteDate.text = date
                        currentNote = updatedNote
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()
        
        dialog.show()
    }

    private fun polishNote(note: Note) {
        if (isPolishing) {
            Toast.makeText(this, "正在润色中，请稍候", Toast.LENGTH_SHORT).show()
            return
        }
        isPolishing = true
        binding.polishButton.isEnabled = false

        // 显示加载提示
        Toast.makeText(this, "正在润色...", Toast.LENGTH_SHORT).show()

        val prompt = "请帮我润色这篇学习心得，包括总结主要内容和添加一些心情描写：\n\n${note.content}"
        val streamBuilder = StringBuilder()

        SparkApi().chat(prompt, object : SparkApi.Callback {
            override fun onPartial(partialText: String) {
                streamBuilder.append(partialText)
                binding.noteContent.text = streamBuilder.toString()
            }

            override fun onSuccess(response: String) {
                updateNoteContent(note, response)
                isPolishing = false
                binding.polishButton.isEnabled = true
            }

            override fun onError(error: String) {
                Log.e("NoteDetailActivity", "Polish failed: $error")
                Toast.makeText(this@NoteDetailActivity, "润色失败: $error", Toast.LENGTH_SHORT).show()
                isPolishing = false
                binding.polishButton.isEnabled = true
            }
        })
    }

    private fun updateNoteContent(note: Note, polishedContent: String) {
        // 更新UI
        binding.noteContent.text = polishedContent
        
        // 更新数据库
        val updatedNote = note.copy(content = polishedContent)
        val success = noteDao.editNote(updatedNote)
        
        if (success) {
            currentNote = updatedNote
            Toast.makeText(this, "润色成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
        }
    }



}
