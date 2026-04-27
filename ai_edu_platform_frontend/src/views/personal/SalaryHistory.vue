<!-- 薪资报告历史页面 -->
<template>
  <div class="salary-history-container">
    <h2>薪资评估历史</h2>
    
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

const router = useRouter()
const loading = ref(false)
const historyList = ref([])

// 页面加载时获取历史记录
onMounted(async () => {
  await loadHistory()
})

// 加载历史记录
const loadHistory = async () => {
  loading.value = true
  try {
    const response = await getSalaryHistory()
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

// 查看详情
const viewDetail = (reportId) => {
  localStorage.setItem('salaryReportId', reportId)
  router.push('/salary/report')
}

// 返回个人中心
const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.salary-history-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  animation: tech-fade-in 0.6s ease-out;
}

.salary-history-container h2 {
  margin-bottom: 20px;
  text-align: center;
  font-size: 28px;
  font-weight: 800;
  background: var(--tech-gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.history-card {
  margin-bottom: 20px;
  background: var(--tech-glass-bg-strong);
  backdrop-filter: blur(var(--tech-glass-blur));
  -webkit-backdrop-filter: blur(var(--tech-glass-blur));
  border: 1px solid var(--tech-glass-border);
  border-radius: var(--tech-radius-lg);
  box-shadow: var(--tech-card-shadow);
}

.history-card :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-border-color: var(--tech-divider);
  background: transparent;
  color: var(--tech-text-primary);
}

.history-card :deep(.el-table th.el-table__cell) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: var(--tech-text-secondary);
  border-color: var(--tech-divider);
}

.history-card :deep(.el-table td.el-table__cell) {
  border-color: var(--tech-divider);
  color: var(--tech-text-primary);
}

.history-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(255, 255, 255, 0.02);
}

.button-section {
  display: flex;
  justify-content: center;
}
</style>
