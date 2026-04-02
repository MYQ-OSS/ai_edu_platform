<!-- 薪资报告详情页 -->
<template>
  <div class="salary-detail-container">
    <h2>薪资报告详情</h2>
    <div class="detail-card">
      <el-form :model="salaryDetail" label-width="120px">
        <el-form-item label="报告ID">
          <el-input v-model="salaryDetail.id" disabled />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="salaryDetail.position" disabled />
        </el-form-item>
        <el-form-item label="预估薪资">
          <el-input v-model="salaryDetail.salary" disabled />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="salaryDetail.industry" disabled />
        </el-form-item>
        <el-form-item label="工作经验">
          <el-input v-model="salaryDetail.experience" disabled />
        </el-form-item>
        <el-form-item label="学历要求">
          <el-input v-model="salaryDetail.education" disabled />
        </el-form-item>
        <el-form-item label="技能要求">
          <el-input v-model="salaryDetail.skills" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="薪资趋势">
          <el-input v-model="salaryDetail.trend" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="生成时间">
          <el-input v-model="salaryDetail.created_at" disabled />
        </el-form-item>
        <el-form-item>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const reportId = route.params.id

const salaryDetail = reactive({
  id: '',
  position: '',
  salary: '',
  industry: '',
  experience: '',
  education: '',
  skills: '',
  trend: '',
  created_at: ''
})

// 页面加载时获取薪资报告详情
onMounted(async () => {
  await loadSalaryDetail()
})

const loadSalaryDetail = async () => {
  try {
    // 模拟获取薪资报告详情数据
    // 实际项目中应该调用API获取真实数据
    const mockData = {
      1: {
        id: '1',
        position: '前端工程师',
        salary: '15000-20000元',
        industry: '互联网',
        experience: '1-3年',
        education: '本科及以上',
        skills: 'Vue3, React, JavaScript, TypeScript, HTML/CSS',
        trend: '前端工程师薪资呈上升趋势，特别是掌握现代前端框架和TypeScript的开发者薪资更高。',
        created_at: '2026-03-29 09:30:00'
      },
      2: {
        id: '2',
        position: '后端工程师',
        salary: '18000-25000元',
        industry: '互联网',
        experience: '3-5年',
        education: '本科及以上',
        skills: 'Java, Spring Boot, MySQL, Redis, Docker',
        trend: '后端工程师薪资稳定增长，特别是具备微服务架构经验和云原生技术的开发者薪资更高。',
        created_at: '2026-03-27 11:20:00'
      }
    }
    
    if (mockData[reportId]) {
      Object.assign(salaryDetail, mockData[reportId])
    } else {
      ElMessage.error('未找到薪资报告详情')
    }
  } catch (error) {
    ElMessage.error('获取薪资报告详情失败')
  }
}

const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.salary-detail-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.salary-detail-container h2 {
  margin-bottom: 20px;
  color: #303133;
  text-align: center;
}

.detail-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-form-item {
  margin-bottom: 15px;
}
</style>