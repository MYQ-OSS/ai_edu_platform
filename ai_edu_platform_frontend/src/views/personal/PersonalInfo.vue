<!-- 个人中心页面 -->
<template>
  <div class="personal-center-container">
    <h2>个人中心</h2>
    
    <!-- 顶部个人信息 -->
    <div class="top-info-card">
      <div class="avatar-section">
        <el-avatar :size="100" class="avatar" :src="'/程序员.png'">
          {{ userForm?.username?.charAt(0) || 'U' }}
        </el-avatar>
      </div>
      <div class="user-basic-info">
        <div class="info-row">
          <label class="info-label">用户名：</label>
          <span class="info-value username">{{ userForm?.username || '未登录' }}</span>
        </div>
        <div class="info-row">
          <label class="info-label">身份：</label>
          <span class="info-value">{{ userForm?.identity || '未设置' }}</span>
        </div>
        <div class="info-row">
          <label class="info-label">期望薪资：</label>
          <span class="info-value salary">{{ userForm?.salary ? userForm.salary + '元' : '未设置' }}</span>
        </div>
        <div class="info-row">
          <label class="info-label">答题统计：</label>
          <span class="info-value">答题次数：<strong>{{ userForm?.answerTimes || 0 }}</strong> | 平均得分：<strong class="score">{{ userForm?.averageScore || 0 }}</strong></span>
        </div>
        <div class="info-row experience-row">
          <label class="info-label">项目经历：</label>
          <span class="info-value experience-text">{{ userForm?.experience || '未设置' }}</span>
        </div>
      </div>
      <div class="button-section">
        <el-button type="primary" @click="handleEditInfo" class="edit-btn" size="large">
          <el-icon><Edit /></el-icon>
          编辑信息
        </el-button>
        <el-button @click="goBack" class="back-btn" size="large">
          <el-icon><Back /></el-icon>
          返回首页
        </el-button>
      </div>
    </div>
    
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="tabs-container">
      <el-tab-pane label="答题历史" name="answer-history">
        <div class="tab-content">
          <el-table :data="answerHistoryList" style="width: 100%" stripe>
            <el-table-column prop="score" label="得分" width="100" align="center">
              <template #default="scope">
                <span class="score-badge">{{ scope.row.score }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="正确率" width="100" align="center">
              <template #default="scope">
                <span class="accuracy-badge">{{ scope.row.accuracy }}%</span>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="评价" min-width="250" show-overflow-tooltip align="center" />
            <el-table-column prop="createTime" label="答题时间" min-width="180" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="default" @click="viewAnswerDetail(scope.row.recordId)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="answerHistoryList.length === 0" class="empty-state">
            <el-empty description="暂无答题历史" />
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="薪资报告" name="salary-report">
        <div class="tab-content">
          <el-table :data="salaryReportList" style="width: 100%" stripe>
            <el-table-column prop="direction" label="技术方向" width="150" align="center" />
            <el-table-column prop="salaryRange" label="预估薪资" width="150" align="center">
              <template #default="scope">
                <span class="salary-badge">{{ scope.row.salaryRange }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="city" label="城市" width="120" align="center" />
            <el-table-column prop="createTime" label="生成时间" min-width="200" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="default" @click="viewSalaryDetail(scope.row.id)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="salaryReportList.length === 0" class="empty-state">
            <el-empty description="暂无薪资报告" />
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的收藏" name="my-collect">
        <div class="tab-content">
          <el-table :data="collectList" style="width: 100%" stripe v-loading="collectLoading">
            <el-table-column prop="questionName" label="题目名称" min-width="250" show-overflow-tooltip align="center" />
            <el-table-column prop="direction" label="技术方向" width="150" align="center" />
            <el-table-column prop="targetSalary" label="目标薪资" width="120" align="center">
              <template #default="scope">
                <span class="salary-badge">{{ scope.row.targetSalary }}元</span>
              </template>
            </el-table-column>
            <el-table-column label="答题状态" width="120" align="center">
              <template #default="scope">
                <el-tag :type="isAnswered(scope.row.questionId) ? 'success' : 'info'" size="small">
                  {{ isAnswered(scope.row.questionId) ? '已答题' : '未答题' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="collectTime" label="收藏时间" min-width="180" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.collectTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button type="primary" size="default" @click="viewCollectDetail(scope.row.questionId)" :loading="collectLoading">查看详情</el-button>
                  <el-button type="danger" size="default" @click="cancelCollect(scope.row.questionId)" :loading="collectLoading">取消收藏</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="collectList.length === 0" class="empty-state">
            <el-empty description="暂无收藏题目" />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Back } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/userStore'
import { getCollectList, toggleCollect } from '../../api/questionApi'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const userFormRef = ref(null)

// 根据 URL 参数设置默认激活的标签页
const getDefaultTab = () => {
  const tab = route.query.tab
  if (tab === 'salary-report') return 'salary-report'
  if (tab === 'my-collect') return 'my-collect'
  return 'answer-history' // 默认显示答题历史
}

const activeTab = ref(getDefaultTab())

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

// 收藏列表数据
const collectList = ref([])
const collectLoading = ref(false)

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
    await loadCollectList()
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

// 判断题目是否已答题
const isAnswered = (questionId) => {
  return answerHistoryList.value.some(record => record.questionId === questionId)
}

// 加载收藏列表
const loadCollectList = async () => {
  try {
    collectLoading.value = true
    const response = await getCollectList()
    if (response.code === 200) {
      collectList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    collectLoading.value = false
  }
}

// 查看收藏题目详情
const viewCollectDetail = async (questionId) => {
  try {
    // 先检查该题目是否有答题记录
    const answerRecord = answerHistoryList.value.find(record => record.questionId === questionId)
    
    if (answerRecord) {
      // 有答题记录，跳转到答题详情页
      router.push(`/personal/answer-detail/${answerRecord.recordId}`)
    } else {
      // 没有答题记录，从收藏列表中获取题目信息并跳转到答题页面
      const collectItem = collectList.value.find(item => item.questionId === questionId)
      if (!collectItem) {
        ElMessage.error('题目不存在')
        return
      }
      
      // 构造题目数据并存储到localStorage
      const questionData = {
        questionId: collectItem.questionId,
        questionName: collectItem.questionName,
        questionDesc: collectItem.questionDesc,
        direction: collectItem.direction,
        targetSalary: collectItem.targetSalary,
        options: collectItem.options,
        timeLimit: 0 // 收藏题目默认不限时
      }
      
      localStorage.setItem('currentQuestion', JSON.stringify(questionData))
      
      // 跳转到答题页面，并标记来源为个人中心
      router.push({
        path: '/question/answer',
        query: { from: 'personal' }
      })
    }
  } catch (error) {
    ElMessage.error('跳转失败')
  }
}

// 取消收藏
const cancelCollect = async (questionId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    collectLoading.value = true
    const response = await toggleCollect(questionId)
    if (response.code === 200) {
      ElMessage.success('取消收藏成功')
      // 刷新收藏列表
      await loadCollectList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消收藏失败')
    }
  } finally {
    collectLoading.value = false
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  try {
    const date = new Date(timeStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (e) {
    return timeStr
  }
}
</script>

<style scoped>
.personal-center-container {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
}

.personal-center-container h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  font-size: 28px;
  font-weight: 600;
}

/* 顶部个人信息卡片 */
.top-info-card {
  display: flex;
  align-items: center;
  gap: 30px;
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 40px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  object-fit: cover;
}

.user-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #fafafa;
  border-radius: 8px;
}

.info-label {
  font-size: 16px;
  font-weight: 600;
  color: #606266;
  min-width: 100px;
}

.info-value {
  font-size: 16px;
  color: #303133;
  line-height: 1.6;
}

.info-value.username {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.info-value.salary {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.info-value strong {
  font-weight: 600;
  color: #303133;
}

.info-value .score {
  color: #67c23a;
  font-size: 18px;
}

.experience-row {
  align-items: flex-start;
}

.experience-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
}

.button-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.edit-btn,
.back-btn {
  min-width: 140px;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.edit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653e8f 100%);
}

/* 标签页容器 */
.tabs-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
  overflow: hidden;
  padding: 20px;
  min-height: 400px;
}

.tab-content {
  min-height: 300px;
}

/* 历史记录卡片 */
.history-card,
.report-card {
  /* 已移除，使用统一的 tab-content */
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
}

/* 表格样式 */
.el-table {
  margin-top: 10px;
  font-size: 15px;
}

.el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  font-size: 15px;
}

.el-table td {
  color: #303133;
  font-size: 15px;
}

/* 分数徽章 */
.score-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: #ecf5ff;
  color: #409EFF;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

.accuracy-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: #f0f9ff;
  color: #67c23a;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

/* 薪资徽章 */
.salary-badge {
  display: inline-block;
  padding: 4px 12px;
  background-color: #f0f9ff;
  color: #67c23a;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

/* 按钮样式 */
.el-button {
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button--primary {
  background-color: #409EFF;
  border-color: #409EFF;
}

.el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 收藏列表操作按钮 */
.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personal-center-container {
    padding: 20px;
  }
  
  .top-info-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .avatar-section {
    margin-bottom: 10px;
  }
  
  .user-basic-info {
    width: 100%;
  }
  
  .info-row {
    justify-content: center;
  }
  
  .button-section {
    flex-direction: row;
    width: 100%;
  }
  
  .edit-btn,
  .back-btn {
    flex: 1;
  }
}
</style>