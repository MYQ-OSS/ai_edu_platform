<!-- 技术挑战输入前提信息页 -->
<template>
  <div class="question-input-container">
    <h2>技术挑战 - 输入前提信息</h2>
    <div class="input-card">
      <el-form :model="inputForm" :rules="rules" ref="inputFormRef" label-width="120px">
        <!-- 必填项 -->
        <el-form-item label="身份" prop="identity" required>
          <el-input v-model="inputForm.identity" placeholder="请输入您的身份（如：学生、前端工程师等）" />
        </el-form-item>
        
        <el-form-item label="技术方向" prop="techDirection" required>
          <el-select v-model="inputForm.techDirection" placeholder="请选择技术方向">
            <el-option label="前端开发" value="frontend" />
            <el-option label="后端开发" value="backend" />
            <el-option label="移动端开发" value="mobile" />
            <el-option label="数据科学" value="data" />
            <el-option label="DevOps" value="devops" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="期望薪资" prop="expectedSalary" required>
          <el-input v-model.number="inputForm.expectedSalary" type="number" placeholder="请输入期望薪资（元）" />
        </el-form-item>
        
        <!-- 非必填项 -->
        <el-form-item label="限定时间">
          <el-input v-model.number="inputForm.timeLimit" type="number" placeholder="请输入限定时间（分钟）" />
        </el-form-item>
        
        <el-form-item label="就业城市">
          <el-input v-model="inputForm.city" placeholder="请输入就业城市" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">开始挑战</el-button>
          <el-button @click="goBack">返回首页</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const inputFormRef = ref(null)
const loading = ref(false)

const inputForm = reactive({
  identity: '',
  techDirection: '',
  expectedSalary: '',
  timeLimit: '',
  city: ''
})

const rules = {
  identity: [
    { required: true, message: '请输入身份', trigger: 'blur' },
    { max: 50, message: '身份长度不超过50位', trigger: 'blur' }
  ],
  techDirection: [
    { required: true, message: '请选择技术方向', trigger: 'change' }
  ],
  expectedSalary: [
    { required: true, message: '请输入期望薪资', trigger: 'blur' },
    { type: 'number', message: '请输入有效的薪资', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  await inputFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 模拟提交数据
        // 实际项目中应该调用API提交数据
        setTimeout(() => {
          loading.value = false
          // 保存表单数据到localStorage，以便在答题页使用
          localStorage.setItem('questionInputData', JSON.stringify(inputForm))
          ElMessage.success('前提信息提交成功')
          router.push('/question/answer')
        }, 1000)
      } catch (error) {
        loading.value = false
        ElMessage.error('提交失败，请稍后重试')
      }
    }
  })
}

const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.question-input-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.question-input-container h2 {
  margin-bottom: 20px;
  color: #303133;
  text-align: center;
}

.input-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-form-item {
  margin-bottom: 15px;
}
</style>