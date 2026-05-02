<!-- 个人中心页面 - Cyberpunk 2.0 -->
<template>
  <div class="personal-center-container">
    <ParticleBackground :zIndex="0" :particleCount="40" particleColor="mixed" :speed="0.3" />
    <h2 class="page-title gradient-text">个人中心</h2>

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
        <el-button type="primary" @click="goBack" class="back-btn" size="large">
          <el-icon><Back /></el-icon>
          返回首页
        </el-button>
      </div>
    </div>

    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab" class="tabs-container">
      <el-tab-pane label="答题历史" name="answer-history">
        <div class="tab-content">
          <el-table :data="answerHistoryList" style="width: 100%">
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
            <el-table-column prop="comment" label="评价" min-width="250" align="center">
              <template #default="scope">
                <el-tooltip :content="scope.row.comment" placement="top" effect="dark" popper-class="custom-tooltip">
                  <div class="comment-cell">{{ scope.row.comment }}</div>
                </el-tooltip>
              </template>
            </el-table-column>
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
          <el-table :data="salaryReportList" style="width: 100%">
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
          <el-table :data="collectList" style="width: 100%" v-loading="collectLoading">
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

      <el-tab-pane label="学习统计" name="learning-statistics">
        <div class="tab-content">
          <div class="statistics-hint">
            <el-icon class="hint-icon"><TrendCharts /></el-icon>
            <p>查看详细的学习统计图表，包括得分趋势和正确率分析</p>
            <el-button type="primary" @click="goToLearningStatistics" size="large">
              <el-icon><DataAnalysis /></el-icon>
              查看学习统计
            </el-button>
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
import { Edit, Back, TrendCharts, DataAnalysis } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/userStore'
import { getCollectList, toggleCollect } from '../../api/questionApi'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

defineOptions({ name: 'PersonalInfo' })

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const userFormRef = ref(null)

const getDefaultTab = () => {
  const tab = route.query.tab
  if (tab === 'salary-report') return 'salary-report'
  if (tab === 'my-collect') return 'my-collect'
  if (tab === 'learning-statistics') return 'learning-statistics'
  return 'answer-history'
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

const answerHistoryList = ref([])
const salaryReportList = ref([])
const collectList = ref([])
const collectLoading = ref(false)
const accuracyTrendData = ref([])

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
    if (userStore.isLoggedIn) {
      ElMessage.error('获取个人信息失败')
    }
  }
}

const loadLearningHistory = async () => {
  try {
    const response = await userStore.getLearningHistory()
    if (response.code === 200) {
      const data = response.data
      answerHistoryList.value = data.quizRecords || []
      salaryReportList.value = data.salaryReports || []
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
  router.push('/personal/edit-info')
}

const goBack = () => {
  router.push('/home')
}

const viewAnswerDetail = (id) => {
  router.push(`/personal/answer-detail/${id}`)
}

const viewSalaryDetail = (id) => {
  router.push(`/personal/salary-detail/${id}`)
}

const isAnswered = (questionId) => {
  return answerHistoryList.value.some(record => record.questionId === questionId)
}

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

const viewCollectDetail = async (questionId) => {
  try {
    const answerRecord = answerHistoryList.value.find(record => record.questionId === questionId)
    if (answerRecord) {
      router.push(`/personal/answer-detail/${answerRecord.recordId}`)
    } else {
      const collectItem = collectList.value.find(item => item.questionId === questionId)
      if (!collectItem) {
        ElMessage.error('题目不存在')
        return
      }
      const questionData = {
        questionId: collectItem.questionId,
        questionName: collectItem.questionName,
        questionDesc: collectItem.questionDesc,
        direction: collectItem.direction,
        targetSalary: collectItem.targetSalary,
        options: collectItem.options,
        timeLimit: 0
      }
      localStorage.setItem('currentQuestion', JSON.stringify(questionData))
      router.push({ path: '/question/answer', query: { from: 'personal' } })
    }
  } catch (error) {
    ElMessage.error('跳转失败')
  }
}

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

const goToLearningStatistics = () => {
  router.push('/personal/learning-statistics')
}

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
  padding: 30px 40px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
  position: relative;
}

.page-title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 30px;
  font-weight: 900;
  letter-spacing: 3px;
  position: relative;
  z-index: 1;
}

/* 顶部个人信息 */
.top-info-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  padding: 32px;
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
  animation: breathe 5s ease-in-out infinite;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar {
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: white;
  font-size: 36px;
  font-weight: 800;
  border: 2px solid var(--neon-cyan);
  box-shadow: var(--glow-cyan), 0 0 20px rgba(0, 212, 255, 0.4);
  object-fit: cover;
  transition: all var(--transition-base);
}

.top-info-card:hover .avatar {
  transform: scale(1.08);
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

.user-basic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  background: rgba(0, 212, 255, 0.03);
  border-radius: var(--radius-xs);
  border: 1px solid var(--divider);
  transition: all var(--transition-fast);
  font-size: 14px;
}

.info-row:hover {
  background: rgba(0, 212, 255, 0.06);
  border-color: var(--neon-cyan);
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  min-width: 90px;
  display: flex;
  justify-content: center;
  text-align: center;
}

.info-value {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
}

.info-value.username {
  font-size: 18px;
  font-weight: 700;
}

.info-value.salary {
  font-size: 16px;
  font-weight: 700;
  color: var(--neon-cyan);
  text-shadow: var(--glow-cyan);
}

.info-value strong {
  font-weight: 700;
  color: var(--neon-purple);
}

.info-value .score {
  color: var(--neon-cyan);
  font-size: 16px;
  text-shadow: var(--glow-cyan);
}

.experience-row {
  align-items: flex-start;
}

.experience-text {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
  color: var(--text-secondary);
}

.button-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 180px;
  flex-shrink: 0;
}

.button-section :deep(.el-button) {
  width: 100% !important;
  height: 42px !important;
  padding: 0 !important;
  margin: 0 !important;
  display: flex !important;
  justify-content: center;
  align-items: center;
  box-sizing: border-box !important;
  border-radius: var(--radius-xs) !important;
}

.button-section :deep(.el-button > span) {
  display: flex !important;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1px;
  padding: 0 !important;
  margin: 0 !important;
  width: 100% !important;
  box-sizing: border-box !important;
}

.button-section :deep(.el-button .el-icon) {
  flex-shrink: 0 !important;
  font-size: 16px !important;
  line-height: 1 !important;
  margin: 0 !important;
  padding: 0 !important;
  display: inline-flex !important;
  align-items: center;
}

.edit-btn {
  background: var(--btn-primary-bg) !important;
  border: none !important;
  color: #fff !important;
}

.edit-btn:hover {
  background: var(--btn-primary-hover) !important;
}

.back-btn {
  background: transparent !important;
  border: 1px solid var(--neon-cyan) !important;
  color: var(--neon-cyan) !important;
}

.back-btn:hover {
  background: rgba(0, 212, 255, 0.1) !important;
  color: var(--neon-cyan) !important;
  box-shadow: var(--glow-cyan) !important;
}

/* 标签页 */
.tabs-container {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  overflow: hidden;
  padding: 16px;
  min-height: 350px;
  animation: breathe 5s ease-in-out infinite;
}

.tabs-container :deep(.el-tabs__header) {
  border-bottom: 1px solid var(--divider);
}

.tabs-container :deep(.el-tabs__item.is-active) {
  color: var(--neon-cyan) !important;
  text-shadow: var(--glow-text-cyan);
  font-weight: 700;
}

.tabs-container :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple)) !important;
  box-shadow: var(--glow-cyan);
  height: 3px !important;
}

.tab-content {
  min-height: 280px;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
}

/* 表格 */
.el-table th.el-table__cell {
  background: rgba(0, 212, 255, 0.05) !important;
  color: var(--neon-cyan) !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(180, 74, 255, 0.02) !important;
}

.el-table__body tr:hover > td {
  background: rgba(0, 212, 255, 0.06) !important;
}

/* 确保tooltip不被遮挡 */
.el-table__cell {
  position: relative;
  z-index: 1;
}

.comment-cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

/* 自定义tooltip样式 */
:deep(.custom-tooltip) {
  max-width: 300px !important;
}

:deep(.custom-tooltip .el-tooltip__trigger) {
  max-width: 100%;
}

/* 徽章 */
.score-badge {
  display: inline-block;
  padding: 3px 10px;
  background: rgba(0, 212, 255, 0.1);
  color: var(--neon-cyan);
  border-radius: var(--radius-xs);
  font-weight: 700;
  font-size: 13px;
  border: 1px solid rgba(0, 212, 255, 0.2);
  text-shadow: var(--glow-text-cyan);
}

.accuracy-badge {
  display: inline-block;
  padding: 3px 10px;
  background: rgba(0, 255, 65, 0.1);
  color: var(--neon-green);
  border-radius: var(--radius-xs);
  font-weight: 700;
  font-size: 13px;
  border: 1px solid rgba(0, 255, 65, 0.2);
  text-shadow: var(--glow-text-green);
}

.salary-badge {
  display: inline-block;
  padding: 3px 10px;
  background: rgba(180, 74, 255, 0.1);
  color: var(--neon-purple);
  border-radius: var(--radius-xs);
  font-weight: 700;
  font-size: 13px;
  border: 1px solid rgba(180, 74, 255, 0.2);
  text-shadow: var(--glow-text-purple);
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.statistics-hint {
  text-align: center;
  padding: 60px 20px;
}

.hint-icon {
  font-size: 56px;
  color: var(--neon-cyan);
  margin-bottom: 16px;
  animation: corner-pulse 3s ease-in-out infinite;
  text-shadow: var(--glow-cyan);
}

.statistics-hint p {
  font-size: 15px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .personal-center-container {
    padding: 20px;
  }

  .top-info-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .avatar-section { margin-bottom: 8px; }
  .user-basic-info { width: 100%; }
  .info-row { justify-content: center; }
  .button-section { flex-direction: row; width: 100%; }
}
</style>
