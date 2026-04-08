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
        <p>答题次数：{{ userForm?.answer_times || 0 }} | 平均得分：{{ userForm?.average_score || 0 }}</p>
      </div>
      <el-button type="primary" @click="handleEditInfo" class="edit-btn">编辑信息</el-button>
    </div>
    
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="tabs-container">
      <el-tab-pane label="答题历史" name="answer-history">
        <div class="history-card">
          <el-table :data="answerHistoryList" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="question_type" label="题目类型" width="120" />
            <el-table-column prop="score" label="得分" width="80" />
            <el-table-column prop="created_at" label="答题时间" />
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
            <el-table-column prop="position" label="职位" width="120" />
            <el-table-column prop="salary" label="预估薪资" width="120" />
            <el-table-column prop="industry" label="行业" width="120" />
            <el-table-column prop="created_at" label="生成时间" />
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
  salary: '',
  experience: '',
  answer_times: '',
  average_score: ''
})

// 模拟答题历史数据
const answerHistoryList = ref([
  {
    id: 1,
    question_type: '技术挑战题',
    score: 85,
    created_at: '2026-03-28 14:30:00'
  },
  {
    id: 2,
    question_type: '基础知识题',
    score: 92,
    created_at: '2026-03-25 10:15:00'
  },
  {
    id: 3,
    question_type: '技术挑战题',
    score: 78,
    created_at: '2026-03-20 16:45:00'
  }
])

// 模拟薪资报告数据
const salaryReportList = ref([
  {
    id: 1,
    position: '前端工程师',
    salary: '15000-20000元',
    industry: '互联网',
    created_at: '2026-03-29 09:30:00'
  },
  {
    id: 2,
    position: '后端工程师',
    salary: '18000-25000元',
    industry: '互联网',
    created_at: '2026-03-27 11:20:00'
  }
])

const rules = {
  identity: [
    { max: 23, message: '用户身份长度不超过23位', trigger: 'blur' }
  ]
}

// 页面加载时获取个人信息
onMounted(async () => {
  if (userStore.isLoggedIn) {
    await loadUserInfo()
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
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('更新失败，请稍后重试')
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
  
  // 由于我们已经移除了个人信息标签页，这里可以跳转到专门的个人信息编辑页面
  // 或者显示一个提示信息
  ElMessage.info('个人信息编辑功能已移至其他页面')
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