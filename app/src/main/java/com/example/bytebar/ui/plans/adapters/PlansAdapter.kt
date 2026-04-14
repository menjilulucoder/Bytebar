package com.example.bytebar.ui.plans.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bytebar.R
import com.example.bytebar.databinding.ItemPlanBinding
import com.example.bytebar.model.Plan

/**
 * 技能规划列表适配器
 */
class PlansAdapter : RecyclerView.Adapter<PlansAdapter.PlanViewHolder>() {

    private var plans: List<Plan> = mutableListOf()
    private var isDeleteMode = false
    private val selectedPlans = mutableSetOf<Int>()
    
    // 点击事件监听器接口
    interface OnPlanClickListener {
        fun onItemClick(plan: Plan)
        fun onDetailClick(plan: Plan)
        fun onLongClick()
    }
    
    private var clickListener: OnPlanClickListener? = null

    /**
     * 设置规划数据
     */
    fun setPlans(plans: List<Plan>) {
        this.plans = plans
        notifyDataSetChanged()
    }

    /**
     * 切换删除模式
     */
    fun toggleDeleteMode(isDeleteMode: Boolean) {
        this.isDeleteMode = isDeleteMode
        if (!isDeleteMode) {
            selectedPlans.clear()
        }
        notifyDataSetChanged()
    }

    /**
     * 获取选中的规划ID列表
     */
    fun getSelectedPlanIds(): List<Int> = selectedPlans.toList()
    
    /**
     * 设置点击事件监听器
     */
    fun setOnPlanClickListener(listener: OnPlanClickListener) {
        this.clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.bind(plan)
    }

    override fun getItemCount(): Int = plans.size

    /**
     * 规划列表项ViewHolder
     */
    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: Plan) {
            // 绑定数据
            binding.planTitle.text = plan.title
            binding.planTime.text = plan.startTime
            binding.planDuration.text = "${plan.duration}分钟"
            binding.planStatus.text = if (plan.isCompleted) "已完成" else "未完成"

            // 设置背景颜色
            if (plan.isCompleted) {
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.teal_200))
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }

            // 设置复选框状态
            binding.checkBox.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
            binding.checkBox.isChecked = selectedPlans.contains(plan.id)

            // 设置点击事件
            binding.root.setOnClickListener {
                if (isDeleteMode) {
                    // 删除模式下点击切换复选框状态
                    if (selectedPlans.contains(plan.id)) {
                        selectedPlans.remove(plan.id)
                    } else {
                        selectedPlans.add(plan.id)
                    }
                    notifyItemChanged(adapterPosition)
                } else {
                    // 正常模式下点击
                    clickListener?.onItemClick(plan)
                }
            }

            // 设置长按事件（进入删除模式）
            binding.root.setOnLongClickListener {
                clickListener?.onLongClick()
                true
            }

            // 设置详情按钮点击事件
            binding.detailButton.setOnClickListener {
                clickListener?.onDetailClick(plan)
            }

            // 设置复选框点击事件
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedPlans.add(plan.id)
                } else {
                    selectedPlans.remove(plan.id)
                }
            }
        }
    }
}