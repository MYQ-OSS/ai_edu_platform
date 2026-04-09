<!-- 个人中心页面 -->
<template>
  <div class="personal-center-container">
    <h2>个人中心</h2>
    
    <!-- 顶部个人信息 -->
    <div class="top-info-card">
      <el-avatar :size="80" class="avatar">
        {{ userForm?.username?.charAt(0) || 'U' }}
      </el-avatar>
      <div class="user-basic-info">
        <h3>{{ userForm?.username || '未登录' }}</h3>
        <p>{{ userForm?.identity || '未设置' }}</p>
        <p>期望薪资：{{ userForm?.salary ? userForm.salary + '元' : '未设置' }}</p>
        <p>答题次数：{{ userForm?.answerTimes || 0 }} | 平均得分：{{ userForm?.averageScore || 0 }}</p>
      </div>
      <el-button type="primary" @click="handleEditInfo" class="edit-btn">编辑信息</el-button>
      <el-button @click="goBack" class="back-btn">返回首页</el-button>
    </div>
    
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="tabs-container">
      <el-tab-pane label="答题历史" name="answer-history">
        <div class="history-card">
          <el-table :data="answerHistoryList" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="questionType" label="题目类型" width="120" />
            <el-table-column prop="score" label="得分" width="80" />
            <el-table-column prop="createTime" label="答题时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewAnswerDetail(scope.row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="answerHistoryList.length === 0" class="empty-state">
            暂无答题历史
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="薪资报告" name="salary-report">
        <div class="report-card">
          <el-table :data="salaryReportList" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="direction" label="技术方向" width="120" />
            <el-table-column prop="salaryRange" label="预估薪资" width="120" />
            <el-table-column prop="city" label="城市" width="120" />
            <el-table-column prop="createTime" label="生成时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewSalaryDetail(scope.row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="salaryReportList.length === 0" class="empty-state">
            暂无薪资报告
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const userFormRef = ref(null)
const activeTab = ref('answer-history')

const userForm = reactive({
  id: '',
  username: '',
  identity: '',
  salary: null,
  experience: '',
  answerTimes: 0,
  averageScore: 0,
  createTime: ''
})

// 答题历史数据
const answerHistoryList = ref([])

// 薪资报告数据
const salaryReportList = ref([])

// 正确率趋势数据（用于图表）
const accuracyTrendData = ref([])

const rules = {
  identity: [
    { max: 23, message: '用户身份长度不超过23位', trigger: 'blur' }
  ]
}

// 页面加载时获取个人信息和学习足迹
onMounted(async () => {
  if (userStore.isLoggedIn) {
    await loadUserInfo()
    await loadLearningHistory()
  }
})

const loadUserInfo = async () => {
  try {
    const response = await userStore.fetchUserInfo()
    if (response.code === 200) {
      Object.assign(userForm, response.data)
    }
  } catch (error) {
    // 未登录时不显示错误信息
    if (userStore.isLoggedIn) {
      ElMessage.error('获取个人信息失败')
    }
  }
}

// 加载学习足迹
const loadLearningHistory = async () => {
  try {
    const response = await userStore.getLearningHistory()
    if (response.code === 200) {
      const data = response.data
      // 赋值答题记录
      answerHistoryList.value = data.quizRecords || []
      // 赋值薪资报告
      salaryReportList.value = data.salaryReports || []
      // 赋值正确率趋势
      accuracyTrendData.value = data.accuracyTrend || []
    }
  } catch (error) {
    ElMessage.error('获取学习足迹失败')
  }
}

const handleUpdate = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.info('请先登录后再修改个人信息')
    router.push('/login')
    return
  }
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await userStore.editUserInfo(userForm)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
          // 重新获取用户信息以更新本地数据
          await loadUserInfo()
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '更新失败，请稍后重试')
      }
    }
  })
}

const handleEditInfo = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.info('请先登录后再编辑个人信息')
    router.push('/login')
    return
  }
  
  // 跳转到个人信息编辑页面
  router.push('/personal/edit-info')
}

const goBack = () => {
  router.push('/home')
}

const handleLogout = () => {
  if (userStore.isLoggedIn) {
    userStore.logout()
    ElMessage.success('退出登录成功')
  }
  router.push('/login')
}

const viewAnswerDetail = (id) => {
  // 跳转到答题详情页
  router.push(`/personal/answer-detail/${id}`)
}

const viewSalaryDetail = (id) => {
  // 跳转到薪资报告详情页
  router.push(`/personal/salary-detail/${id}`)
}
</script>

<style scoped>
.personal-center-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.personal-center-container h2 {
  margin-bottom: 20px;
  color: #303133;
  text-align: center;
}

/* 顶部个人信息卡片 */
.top-info-card {
  display: flex;
  align-items: center;
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.avatar {
  margin-right: 30px;
  background-color: #409EFF;
  color: white;
  font-size: 36px;
  font-weight: bold;
}

.user-basic-info {
  flex: 1;
}

.user-basic-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 20px;
  font-weight: bold;
}

.user-basic-info p {
  margin: 5px 0;
  color: #606266;
  line-height: 1.5;
}

.edit-btn {
  margin-left: 20px;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
}

.back-btn {
  margin-left: 10px;
  padding: 10px 20px;
  font-size: 16px;
}

/* 标签页容器 */
.tabs-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 信息卡片 */
.info-card {
  padding: 30px;
}

/* 历史记录卡片 */
.history-card,
.report-card {
  padding: 20px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 16px;
}

/* 表格样式 */
.el-table {
  margin-top: 20px;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-info-card {
    flex-direction: column;
    text-align: center;
  }
  
  .avatar {
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .user-basic-info {
    margin-bottom: 20px;
  }
  
  .edit-btn {
    margin-left: 0;
  }
}
</style>