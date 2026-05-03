<!-- 薪资报告历史 - Cyberpunk 2.0 -->
<template>
  <div class="salary-history-container">
    <h2 class="page-title gradient-text">薪资评估历史</h2>

    <el-card class="history-card" v-loading="loading">
      <el-empty v-if="!loading && historyList.length === 0" description="暂无薪资评估记录" />
      <el-table v-else :data="historyList" style="width: 100%">
        <el-table-column prop="id" label="报告ID" width="100" />
        <el-table-column prop="salaryRange" label="预估薪资" width="150">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.salaryRange }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评估时间" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="button-section">
      <el-button @click="goBack">返回个人中心</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSalaryHistory } from '../../api/salaryApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const historyList = ref([])

onMounted(async () => {
  await loadHistory()
})

const loadHistory = async () => {
  loading.value = true
  try {
    const uid = userStore.userInfo?.id
    if (!uid) {
      ElMessage.error('请先登录')
      return
    }
    const response = await getSalaryHistory(uid)
    if (response.code === 200) {
      historyList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取历史记录失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取历史记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const viewDetail = (reportId) => {
  localStorage.setItem('salaryReportId', reportId)
  router.push('/salary/report')
}

const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.salary-history-container {
  padding: 30px 40px;
  max-width: 1200px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
}

.page-title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 28px;
  font-weight: 900;
  letter-spacing: 2px;
}

.history-card {
  margin-bottom: 20px;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
}

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

.button-section {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .salary-history-container { padding: 20px; }
}
</style>
