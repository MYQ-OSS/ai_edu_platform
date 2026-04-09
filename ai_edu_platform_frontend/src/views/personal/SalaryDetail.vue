<!-- 薪资报告详情页 -->
<template>
  <div class="salary-detail-container">
    <h2>薪资报告详情</h2>
    <div class="detail-card">
      <div class="detail-section">
        <div class="detail-row">
          <div class="detail-item">
            <label class="detail-label">技术方向：</label>
            <span class="detail-value">{{ salaryDetail.direction }}</span>
          </div>
          <div class="detail-item" v-if="salaryDetail.city">
            <label class="detail-label">目标城市：</label>
            <span class="detail-value">{{ salaryDetail.city }}</span>
          </div>
          <div class="detail-item">
            <label class="detail-label">生成时间：</label>
            <span class="detail-value">{{ salaryDetail.createTime }}</span>
          </div>
        </div>
        <div class="detail-item full-width">
          <label class="detail-label">预估薪资：</label>
          <span class="detail-value salary">{{ salaryDetail.salaryRange }}</span>
        </div>
        <div class="detail-item full-width" v-if="salaryDetail.experience">
          <label class="detail-label">工作经验：</label>
          <div class="detail-text">{{ salaryDetail.experience }}</div>
        </div>
        <div class="detail-item full-width" v-if="salaryDetail.aiSuggestion">
          <label class="detail-label">AI建议：</label>
          <div class="detail-text trend">{{ salaryDetail.aiSuggestion }}</div>
        </div>
      </div>
      <div class="button-section">
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSalaryReport } from '../../api/salaryApi'

const router = useRouter()
const route = useRoute()
const reportId = route.params.id

const salaryDetail = reactive({
  id: '',
  userId: '',
  direction: '',
  city: '',
  experience: '',
  salaryRange: '',
  aiSuggestion: '',
  createTime: ''
})

const loading = ref(false)

// 页面加载时获取薪资报告详情
onMounted(async () => {
  await loadSalaryDetail()
})

const loadSalaryDetail = async () => {
  if (!reportId) {
    ElMessage.error('缺少薪资报告ID')
    return
  }
  
  loading.value = true
  try {
    const response = await getSalaryReport(reportId)
    if (response.code === 200) {
      Object.assign(salaryDetail, response.data)
    } else {
      ElMessage.error(response.msg || '获取薪资报告详情失败')
      router.push('/personal/info')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取薪资报告详情失败，请稍后重试')
    router.push('/personal/info')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.salary-detail-container {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
}

.salary-detail-container h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  font-size: 28px;
  font-weight: 600;
}

.detail-card {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
}

.detail-section {
  margin-bottom: 30px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 8px;
}

.detail-item.full-width {
  flex-direction: column;
  align-items: flex-start;
}

.detail-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.detail-row .detail-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 0;
}

.detail-label {
  font-size: 16px;
  font-weight: 600;
  color: #606266;
  min-width: 100px;
  margin-right: 15px;
  line-height: 1.8;
}

.detail-value {
  font-size: 16px;
  color: #303133;
  line-height: 1.8;
  flex: 1;
}

.detail-value.salary {
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
}

.detail-text {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  width: 100%;
  margin-top: 10px;
  padding: 15px;
  background-color: white;
  border-radius: 6px;
  border-left: 4px solid #409EFF;
}

.detail-text.trend {
  border-left-color: #67c23a;
}

.button-section {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>