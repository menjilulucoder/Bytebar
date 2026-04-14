package com.example.bytebar.ui.plans
import android.content.Context
import com.example.bytebar.data.PlansDao
import com.example.bytebar.model.Plan
/**
 * 技能规划模块的Presenter，实现业务逻辑
 */
class PlansPresenter(private val view: PlansContract.View, context: Context) : PlansContract.Presenter {

    private val plansDao = PlansDao(context)

    override fun loadPlans() {
        view.showLoading()
        
        try {
            val plans = plansDao.queryPlans()
            view.showPlans(plans)
        } catch (e: Exception) {
            view.showError("加载数据失败: ${e.message}")
        }
        
        view.hideLoading()
    }

    override fun addPlan() {
        view.showAddPlanDialog()
    }

    override fun editPlan(plan: Plan) {
        view.showEditPlanDialog(plan)
    }

    override fun deletePlan(planId: Int) {
        view.showDeleteConfirmDialog(planId)
    }


    override fun deletePlans(planIds: List<Int>) {
        if (planIds.isEmpty()) {
            view.showError("请选择要删除的规划")
            return
        }
        view.showBatchDeleteConfirmDialog(planIds)
    }

    override fun queryPlans() {
        try {
            view.showQueryDialog()
        } catch (e: Exception) {
            view.showError("查询失败: ${e.message}")
        }
    }

    override fun queryPlansByDate(date: String) {
        view.showLoading()
        
        try {
            val plans = plansDao.queryPlansByDate(date)
            view.showPlans(plans)
            if (plans.isEmpty()) {
                view.showError("未找到该日期的规划")
            }
        } catch (e: Exception) {
            view.showError("按日期查询失败: ${e.message}")
        }
        
        view.hideLoading()
    }

    override fun queryPlansByMonth(month: String) {
        view.showLoading()
        
        try {
            val plans = plansDao.queryPlansByMonth(month)
            view.showPlans(plans)
            if (plans.isEmpty()) {
                view.showError("未找到该月份的规划")
            }
        } catch (e: Exception) {
            view.showError("按月份查询失败: ${e.message}")
        }
        
        view.hideLoading()
    }

    override fun togglePlanCompletion(plan: Plan) {
        try {
            val updatedPlan = plan.copy(isCompleted = !plan.isCompleted)
            val success = plansDao.editPlan(updatedPlan)
            
            if (success) {
                loadPlans() // 重新加载刷新UI
            } else {
                view.showError("更新状态失败")
            }
        } catch (e: Exception) {
            view.showError("更新状态失败: ${e.message}")
        }
    }

    override fun enterDeleteMode() {
        view.enterDeleteMode()
    }

    override fun exitDeleteMode() {
        view.exitDeleteMode()
    }


    override fun destroy() {
        // 清理资源
    }
}//未完成 已完成