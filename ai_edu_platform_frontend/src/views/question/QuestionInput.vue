<!-- 技术挑战输入前提信息页 -->
<template>
  <div class="question-input-container">
    <h2>技术挑战</h2>
    <div class="input-card">
      <el-form :model="inputForm" :rules="rules" ref="inputFormRef" label-width="140px">
        <!-- 必填项 -->
        <el-form-item label="身份定位" prop="identity" required>
          <el-input v-model="inputForm.identity" placeholder="请输入您的身份（如：学生）" size="large" />
        </el-form-item>
        
        <el-form-item label="技术方向" prop="techDirection" required>
          <el-select v-model="inputForm.techDirection" placeholder="请选择技术方向" size="large">
            <el-option 
              v-for="item in techDirections" 
              :key="item.dictCode" 
              :label="item.dictName" 
              :value="item.dictName"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="期望薪资" prop="expectedSalary" required>
          <el-input v-model.number="inputForm.expectedSalary" type="number" placeholder="请输入期望薪资（元/月）" size="large" />
        </el-form-item>
        
        <!-- 非必填项 -->
        <el-form-item label="答题时间限制">
          <el-input v-model.number="inputForm.timeLimit" type="number" placeholder="请输入限定时间（分钟，默认30分钟）" size="large" />
        </el-form-item>
        
        <el-form-item label="目标就业城市">
          <el-input v-model="inputForm.city" placeholder="请输入您期望就业的城市" size="large" />
        </el-form-item>
        
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">开始挑战</el-button>
          <el-button @click="goBack" size="large">返回首页</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateQuiz, getTechDirections } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const inputFormRef = ref(null)
const loading = ref(false)

// 技术方向列表
const techDirections = ref([])

const inputForm = reactive({
  identity: '',
  techDirection: '',
  expectedSalary: '',
  timeLimit: 30,
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

// 页面加载时获取技术方向字典数据
onMounted(async () => {
  await loadTechDirections()
})

// 加载技术方向字典数据
const loadTechDirections = async () => {
  try {
    const response = await getTechDirections()
    if (response.code === 200) {
      techDirections.value = response.data || []
    }
  } catch (error) {
    console.error('获取技术方向失败:', error)
  }
}

const handleSubmit = async () => {
  await inputFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      loading.value = true
      try {
        // 构造请求参数，映射到后端DTO字段
        const requestData = {
          direction: inputForm.techDirection,
          targetSalary: parseInt(inputForm.expectedSalary),
          identity: inputForm.identity || userStore.userInfo?.identity || '',
          city: inputForm.city || userStore.userInfo?.city || '',
          timeLimit: inputForm.timeLimit || 30
        }
        
        // 调用生成题目接口
        const response = await generateQuiz(requestData)
        
        console.log('生成题目响应:', response)
        
        if (response.code === 200) {
          // 检查是否来自降级方案
          const msg = response.msg || ''
          if (msg.includes('已自动从题库中找到')) {
            ElMessage.warning(msg)
          } else {
            ElMessage.success(msg || '题目生成成功')
          }
          
          // 保存题目数据到localStorage
          const questionData = {
            ...response.data,
            timeLimit: inputForm.timeLimit || 0 // 添加限时时间（分钟转秒）
          }
          localStorage.setItem('currentQuestion', JSON.stringify(questionData))
          console.log('跳转到答题页，题目数据:', response.data)
          router.push('/question/answer')
        } else {
          ElMessage.error(response.msg || '题目生成失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '题目生成失败，请稍后重试')
      } finally {
        loading.value = false
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
  padding: 40px;
  max-width: 900px;
  margin: 0 auto;
}

.question-input-container h2 {
  margin-bottom: 40px;
  color: #303133;
  text-align: center;
  font-size: 32px;
  font-weight: 600;
}

.input-card {
  background-color: white;
  padding: 50px;
  border-radius: 12px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
}

.el-form-item {
  margin-bottom: 30px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}
</style>