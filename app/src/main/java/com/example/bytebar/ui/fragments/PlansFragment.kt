package com.example.bytebar.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bytebar.PlanDetailActivity
import com.example.bytebar.data.PlansDao
import com.example.bytebar.databinding.DialogPlanBinding
import com.example.bytebar.databinding.DialogQueryBinding
import com.example.bytebar.databinding.FragmentPlansBinding
import com.example.bytebar.model.Plan
import com.example.bytebar.ui.plans.PlansContract
import com.example.bytebar.ui.plans.PlansPresenter
import com.example.bytebar.ui.plans.adapters.PlansAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * 技能规划页面Fragment
 */
class PlansFragment : Fragment(), PlansContract.View {

    private lateinit var binding: FragmentPlansBinding
    private lateinit var presenter: PlansContract.Presenter
    private lateinit var adapter: PlansAdapter
    
    private lateinit var plansDao: PlansDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlansBinding.inflate(inflater, container, false)
        
        // 初始化Presenter
        presenter = PlansPresenter(this, requireContext())
        
        // 初始化数据库
        plansDao = PlansDao(requireContext())
        
        // 初始化RecyclerView
        setupRecyclerView()
        
        // 加载规划数据
        presenter.loadPlans()
        
        // 设置按钮点击事件
        setupButtonClickListeners()
        
        return binding.root
    }
    
    /**
     * 设置按钮点击事件
     */
    private fun setupButtonClickListeners() {
        // 新建按钮
        binding.addPlanButton.setOnClickListener {
            presenter.addPlan()
        }
        
        // 查询按钮
        binding.queryButton.setOnClickListener {
            presenter.queryPlans()
        }
        
        // 删除按钮
        binding.deleteButton.setOnClickListener {
            val selectedIds = adapter.getSelectedPlanIds()
            presenter.deletePlans(selectedIds)
        }

    }
    
    /**
     * 初始化RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = PlansAdapter()
        binding.plansRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.plansRecyclerView.adapter = adapter
        
        // 设置适配器监听器
        setupAdapterListeners()
    }
    
    /**
     * 设置适配器监听器
     */
    private fun setupAdapterListeners() {
        adapter.setOnPlanClickListener(object : PlansAdapter.OnPlanClickListener {
            override fun onItemClick(plan: Plan) {
                // 处理item点击事件（跳转到详情页）
                Toast.makeText(requireContext(), "点击了规划: ${plan.title}", Toast.LENGTH_SHORT).show()
            }
            
            override fun onDetailClick(plan: Plan) {
                val intent = Intent(requireContext(), PlanDetailActivity::class.java)
                intent.putExtra("plan_id", plan.id)
                intent.putExtra("plan_title", plan.title)
                intent.putExtra("plan_content", plan.content)
                intent.putExtra("plan_start_time", plan.startTime)
                intent.putExtra("plan_duration", plan.duration)
                intent.putExtra("plan_completed", plan.isCompleted)
                startActivity(intent)
            }
            
            override fun onLongClick() {
                presenter.enterDeleteMode()
            }
        })
    }

    override fun showPlans(plans: List<Plan>) {
        adapter.setPlans(plans)
    }

    override fun showAddPlanDialog() {
        showPlanDialog(null)
    }

    override fun showEditPlanDialog(plan: Plan) {
        showPlanDialog(plan)
    }

    override fun showDeleteConfirmDialog(planId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除确认")
            .setMessage("确定要删除这个规划吗？")
            .setPositiveButton("确定") { _, _ ->
                try {
                    val success = plansDao.deletePlan(planId)
                    if (success) {
                        presenter.loadPlans()
                        Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
                    } else {
                        showError("删除失败")
                    }
                } catch (e: Exception) {
                    showError("删除失败: ${e.message}")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun showBatchDeleteConfirmDialog(planIds: List<Int>) {
        AlertDialog.Builder(requireContext())
            .setTitle("批量删除确认")
            .setMessage("确定要删除选中的 ${planIds.size} 个规划吗？")
            .setPositiveButton("确定") { _, _ ->
                try {
                    var successCount = 0
                    for (planId in planIds) {
                        if (plansDao.deletePlan(planId)) {
                            successCount++
                        }
                    }
                    
                    if (successCount == planIds.size) {
                        presenter.exitDeleteMode()
                        presenter.loadPlans()
                        Toast.makeText(requireContext(), "成功删除 $successCount 个规划", Toast.LENGTH_SHORT).show()
                    } else {
                        showError("部分删除失败，成功删除 $successCount 个")
                    }
                } catch (e: Exception) {
                    showError("批量删除失败: ${e.message}")
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun showQueryDialog() {
        // 使用ViewBinding创建查询对话框
        val dialogBinding = DialogQueryBinding.inflate(layoutInflater)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("查询规划")
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .setPositiveButton("查询", null)
            .create()
        
        // 设置查询类型切换
        dialogBinding.queryTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                dialogBinding.dateQueryRadio.id -> {
                    dialogBinding.dateQueryLayout.visibility = View.VISIBLE
                    dialogBinding.monthQueryLayout.visibility = View.GONE
                }
                dialogBinding.monthQueryRadio.id -> {
                    dialogBinding.dateQueryLayout.visibility = View.GONE
                    dialogBinding.monthQueryLayout.visibility = View.VISIBLE
                }
            }
        }
        
        // 为日期输入框添加点击事件
        dialogBinding.dateQueryInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    calendar.set(year, month, dayOfMonth)
                    dialogBinding.dateQueryInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        // 为月份输入框添加点击事件
        dialogBinding.monthQueryInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, _ ->
                    val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    calendar.set(year, month, 1)
                    dialogBinding.monthQueryInput.setText(monthFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        // 自定义查询按钮点击事件
        dialog.setOnShowListener {
            val queryButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            queryButton.setOnClickListener {
                when (dialogBinding.queryTypeGroup.checkedRadioButtonId) {
                    dialogBinding.dateQueryRadio.id -> {
                        val date = dialogBinding.dateQueryInput.text.toString()
                        if (date.isNotEmpty()) {
                            presenter.queryPlansByDate(date)
                            dialog.dismiss()
                        } else {
                            showError("请选择日期")
                        }
                    }
                    dialogBinding.monthQueryRadio.id -> {
                        val month = dialogBinding.monthQueryInput.text.toString()
                        if (month.isNotEmpty()) {
                            presenter.queryPlansByMonth(month)
                            dialog.dismiss()
                        } else {
                            showError("请选择月份")
                        }
                    }
                    else -> {
                        showError("请选择查询类型")
                    }
                }
            }
        }
        
        dialog.show()
    }

    override fun enterDeleteMode() {
        binding.queryButton.visibility = View.GONE
        binding.deleteButton.visibility = View.VISIBLE
        adapter.toggleDeleteMode(true)
    }

    override fun exitDeleteMode() {
        binding.queryButton.visibility = View.VISIBLE
        binding.deleteButton.visibility = View.GONE
        adapter.toggleDeleteMode(false)
    }

    override fun showLoading() {
        binding.loadingProgress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.loadingProgress.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示新建/编辑规划对话框
     */
    private fun showPlanDialog(plan: Plan?) {
        // 使用ViewBinding创建对话框
        val dialogBinding = DialogPlanBinding.inflate(layoutInflater)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .create()
        
        // 设置对话框标题和按钮文本
        if (plan == null) {
            // 新建模式
            dialogBinding.dialogTitle.text = "新建规划"
            dialogBinding.confirmButton.text = "创建"
        } else {
            // 编辑模式
            dialogBinding.dialogTitle.text = "编辑规划"
            dialogBinding.confirmButton.text = "保存"
            
            // 填充现有数据
            dialogBinding.planTitleInput.setText(plan.title)
            dialogBinding.planTimeInput.setText(plan.startTime)
            dialogBinding.planDurationInput.setText(plan.duration.toString())
            dialogBinding.planContentInput.setText(plan.content)
            dialogBinding.planStatusInput.setText(if (plan.isCompleted) "已完成" else "未完成")
        }
        
        // 为时间输入框添加点击事件，弹出日期选择器
        dialogBinding.planTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
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
        
        // 设置确定按钮点击事件
        dialogBinding.confirmButton.setOnClickListener {
            val title = dialogBinding.planTitleInput.text.toString()
            val time = dialogBinding.planTimeInput.text.toString()
            val content = dialogBinding.planContentInput.text.toString()
            val duration = dialogBinding.planDurationInput.text.toString()
            val status = dialogBinding.planStatusInput.text.toString()
            
            // 输入验证
            if (title.isEmpty()) {
                showError("请输入规划标题")
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                showError("请选择时间")
                return@setOnClickListener
            }
            if (duration.isEmpty()) {
                showError("请输入时长")
                return@setOnClickListener
            }
            if (status.isEmpty()) {
                showError("请输入状态")
                return@setOnClickListener
            }
            
            if (status != "已完成" && status != "未完成") {
                showError("状态只能填：已完成 或 未完成")
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                showError("请输入规划内容")
                return@setOnClickListener
            }
            
            val isCompleted = status == "已完成"
            
            try {
                if (plan == null) {
                    // 新建规划
                    val newPlan = Plan(
                        id = 0,
                        title = title,
                        content = content,
                        startTime = time,
                        duration = duration.toInt(),
                        isCompleted = isCompleted
                    )
                    
                    val success = plansDao.addPlan(newPlan)
                    if (success) {
                        presenter.loadPlans()
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "创建成功", Toast.LENGTH_SHORT).show()
                    } else {
                        showError("创建失败")
                    }
                } else {
                    // 编辑规划
                    val updatedPlan = plan.copy(
                        title = title,
                        content = content,
                        startTime = time,
                        duration = duration.toInt(),
                        isCompleted = isCompleted
                    )
                    
                    val success = plansDao.editPlan(updatedPlan)
                    if (success) {
                        presenter.loadPlans()
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
                    } else {
                        showError("保存失败")
                    }
                }
            } catch (e: Exception) {
                showError("操作失败: ${e.message}")
            }
        }
        
        dialog.show()
    }
}//未完成