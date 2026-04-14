package com.example.bytebar

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebar.ai.SparkApi
import com.example.bytebar.data.PlansDao
import com.example.bytebar.databinding.ActivityPlanDetailBinding
import com.example.bytebar.databinding.DialogPlanBinding
import com.example.bytebar.model.Plan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlanDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPlanDetailBinding
    private lateinit var plansDao: PlansDao
    private var planId: Int = 0
    private var currentPlan: Plan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        plansDao = PlansDao(this)

        loadPlanFromDatabase()
        
        binding.editButton.setOnClickListener {
            currentPlan?.let { showEditDialog(it) }
        }
        binding.searchButton.setOnClickListener {
            currentPlan?.let { improveContentWithAI(it) }
        }
    }
    
    private fun loadPlanFromDatabase() {
        planId = intent.getIntExtra("plan_id", 0)
        val plans = plansDao.queryPlans()
        currentPlan = plans.find { it.id == planId }
        
        currentPlan?.let { plan ->
            binding.planTitle.text = plan.title
            binding.planContent.text = if (plan.content.isNotEmpty()) plan.content else "暂无内容"
            binding.planTime.text = plan.startTime
            binding.planDuration.text = "${plan.duration}分钟"
            binding.planStatus.text = if (plan.isCompleted) "已完成" else "未完成"
        }
    }
    
    private fun improveContentWithAI(plan: Plan) {
        val currentContent = plan.content.ifEmpty { "暂无内容" }
        val prompt = "请优化以下学习规划内容，使其更加详细、具体、有可操作性：\n\n当前内容：$currentContent\n\n请直接返回优化后的内容，不需要其他说明。"

        Toast.makeText(this, "AI 正在优化内容...", Toast.LENGTH_SHORT).show()

        SparkApi().chat(prompt, object : SparkApi.Callback {
            override fun onSuccess(response: String) {
                saveAIResponse(plan, response)
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PlanDetailActivity, "AI 调用失败: $error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun saveAIResponse(plan: Plan, aiContent: String) {
        try {
            val updatedPlan = plan.copy(content = aiContent)
            val success = plansDao.editPlan(updatedPlan)

            runOnUiThread {
                if (success) {
                    currentPlan = updatedPlan
                    binding.planContent.text = aiContent
                    Toast.makeText(this, "内容已更新", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEditDialog(plan: Plan) {
        val dialogBinding = DialogPlanBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .create()

        dialogBinding.dialogTitle.text = "编辑规划"
        dialogBinding.confirmButton.text = "保存"

        dialogBinding.planTitleInput.setText(plan.title)
        dialogBinding.planTimeInput.setText(plan.startTime)
        dialogBinding.planDurationInput.setText(plan.duration.toString())
        dialogBinding.planContentInput.setText(plan.content)
        dialogBinding.planStatusInput.setText(if (plan.isCompleted) "已完成" else "未完成")

        dialogBinding.planTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    calendar.set(year, month, dayOfMonth)
                    dialogBinding.planTimeInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        //未完成
        dialogBinding.confirmButton.setOnClickListener {
            val title = dialogBinding.planTitleInput.text.toString()
            val time = dialogBinding.planTimeInput.text.toString()
            val duration = dialogBinding.planDurationInput.text.toString()
            val status = dialogBinding.planStatusInput.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "请输入规划标题", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (duration.isEmpty()) {
                Toast.makeText(this, "请输入时长", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (status.isEmpty()) {
                Toast.makeText(this, "请输入状态", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (status != "已完成" && status != "未完成") {
                Toast.makeText(this, "状态只能填：已完成 或 未完成", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isCompleted = status == "已完成"
            val content = dialogBinding.planContentInput.text.toString()

            try {
                val updatedPlan = plan.copy(
                    title = title,
                    startTime = time,
                    duration = duration.toInt(),
                    content = content,
                    isCompleted = isCompleted
                )

                val success = plansDao.editPlan(updatedPlan)

                if (success) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    
                    currentPlan = updatedPlan
                    binding.planTitle.text = updatedPlan.title
                    binding.planContent.text = if (updatedPlan.content.isNotEmpty()) updatedPlan.content else "暂无内容"
                    binding.planTime.text = updatedPlan.startTime
                    binding.planDuration.text = "${updatedPlan.duration}分钟"
                    binding.planStatus.text = if (updatedPlan.isCompleted) "已完成" else "未完成"
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "操作失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    

}