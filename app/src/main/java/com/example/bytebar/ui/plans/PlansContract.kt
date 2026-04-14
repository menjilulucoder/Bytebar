package com.example.bytebar.ui.plans
import com.example.bytebar.model.Plan
/**
 * 技能规划模块的MVP契约接口
 */
interface PlansContract {
    /**
     * View接口，定义UI操作
     */
    interface View {
        /**
         * 显示规划列表
         */
        fun showPlans(plans: List<Plan>)

        /**
         * 显示新建规划对话框
         */
        fun showAddPlanDialog()

        /**
         * 显示编辑规划对话框
         */
        fun showEditPlanDialog(plan: Plan)

        /**
         * 显示删除确认对话框
         */
        fun showDeleteConfirmDialog(planId: Int)

        /**
         * 显示批量删除确认对话框
         */
        fun showBatchDeleteConfirmDialog(planIds: List<Int>)

        /**
         * 显示查询对话框
         */
        fun showQueryDialog()

        /**
         * 进入删除模式
         */
        fun enterDeleteMode()

        /**
         * 退出删除模式
         */
        fun exitDeleteMode()


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
         * 加载规划列表
         */
        fun loadPlans()

        /**
         * 添加规划
         */
        fun addPlan()

        /**
         * 编辑规划
         */
        fun editPlan(plan: Plan)

        /**
         * 删除规划
         */
        fun deletePlan(planId: Int)

        /**
         * 批量删除规划
         */
        fun deletePlans(planIds: List<Int>)

        /**
         * 查询规划
         */
        fun queryPlans()

        /**
         * 按日期查询规划
         */
        fun queryPlansByDate(date: String)

        /**
         * 按月份查询规划
         */
        fun queryPlansByMonth(month: String)

        /**
         * 切换规划完成状态
         */
        fun togglePlanCompletion(plan: Plan)


        /**
         * 进入删除模式
         */
        fun enterDeleteMode()

        /**
         * 退出删除模式
         */
        fun exitDeleteMode()

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
}