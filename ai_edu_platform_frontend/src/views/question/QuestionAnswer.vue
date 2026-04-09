<!-- 技术挑战答题页 -->
<template>
  <div class="question-answer-container">
    <h2>技术挑战 - 答题页</h2>
    <div class="answer-card">
      <!-- 题目区域 -->
      <div class="question-section">
        <h3>题目：{{ question.title }}</h3>
        <div class="question-content">
          {{ question.content }}
        </div>
      </div>
      
      <!-- 技术栈选择区域 -->
      <div class="tech-stack-section">
        <h3>请选择技术栈</h3>
        <div class="tech-stack-cards">
          <div 
            v-for="tech in techStacks" 
            :key="tech.id"
            class="tech-card"
            :class="{ active: selectedTechStacks.includes(tech.id) }"
            @click="toggleTechStack(tech.id)"
          >
            <div class="tech-name">{{ tech.name }}</div>
          </div>
        </div>
      </div>
      
      <!-- 答题区域 -->
      <div class="answer-section">
        <h3>您的答案</h3>
        <el-input
          v-model="answer"
          type="textarea"
          placeholder="请输入您的答案"
          :rows="8"
        />
      </div>
      
      <!-- 选中的技术栈 -->
      <div v-if="selectedTechStacks.length > 0" class="selected-tech-section">
        <h3>已选择的技术栈</h3>
        <div class="selected-tech-tags">
          <el-tag
            v-for="techId in selectedTechStacks"
            :key="techId"
            closable
            @close="removeTechStack(techId)"
          >
            {{ getTechNameById(techId) }}
          </el-tag>
        </div>
      </div>
      
      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交答案</el-button>
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitQuiz } from '../../api/questionApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const answer = ref('')
const selectedTechStacks = ref([])

// 题目数据
const question = reactive({
  id: null,
  title: '',
  content: '',
  options: []
})

// 技术栈选项（从题目options解析）
const techStacks = ref([])

// 页面加载时获取题目数据
onMounted(() => {
  const currentQuestion = localStorage.getItem('currentQuestion')
  if (!currentQuestion) {
    ElMessage.warning('请先生成题目')
    router.push('/question/input')
    return
  }
  
  try {
    const questionData = JSON.parse(currentQuestion)
    question.id = questionData.questionId
    question.title = questionData.questionName
    question.content = questionData.questionDesc
    
    // 解析options JSON字符串为数组
    if (questionData.options) {
      try {
        const options = JSON.parse(questionData.options)
        question.options = options
        // 转换为前端展示格式
        techStacks.value = options.map((opt, index) => ({
          id: index + 1,
          name: opt.label,
          value: opt.value
        }))
      } catch (e) {
        console.error('解析题目选项失败:', e)
      }
    }
  } catch (error) {
    ElMessage.error('题目数据格式错误')
    router.push('/question/input')
  }
})

// 切换技术栈选择
const toggleTechStack = (techId) => {
  const index = selectedTechStacks.value.indexOf(techId)
  if (index === -1) {
    selectedTechStacks.value.push(techId)
  } else {
    selectedTechStacks.value.splice(index, 1)
  }
}

// 移除技术栈
const removeTechStack = (techId) => {
  const index = selectedTechStacks.value.indexOf(techId)
  if (index !== -1) {
    selectedTechStacks.value.splice(index, 1)
  }
}

// 根据ID获取技术栈名称
const getTechNameById = (techId) => {
  const tech = techStacks.value.find(t => t.id === techId)
  return tech ? tech.name : ''
}

// 根据ID获取技术栈value
const getTechValueById = (techId) => {
  const tech = techStacks.value.find(t => t.id === techId)
  return tech ? tech.value : ''
}

// 提交答案
const handleSubmit = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!answer.value || !answer.value.trim()) {
    ElMessage.warning('请输入答案')
    return
  }
  
  if (selectedTechStacks.value.length === 0) {
    ElMessage.warning('请至少选择一个技术栈')
    return
  }
  
  if (!question.id) {
    ElMessage.error('题目ID不存在')
    return
  }
  
  loading.value = true
  try {
    // 构造用户选择的选项JSON
    const userOptions = selectedTechStacks.value.map(techId => {
      const tech = techStacks.value.find(t => t.id === techId)
      return {
        label: tech.name,
        value: tech.value
      }
    })
    
    // 调用提交接口
    const requestData = {
      userId: userStore.userInfo?.id,
      questionId: question.id,
      userOptions: JSON.stringify(userOptions),
      userAnswer: answer.value
    }
    
    const response = await submitQuiz(requestData)
    
    if (response.code === 200) {
      ElMessage.success('答题结果提交成功')
      // 保存recordId到localStorage，用于报告页获取详情
      localStorage.setItem('quizRecordId', response.data.recordId)
      router.push('/question/report')
    } else {
      ElMessage.error(response.msg || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '提交失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/question/input')
}
</script>

<style scoped>
.question-answer-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.question-answer-container h2 {
  margin-bottom: 20px;
  color: #303133;
  text-align: center;
}

.answer-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.question-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.question-section h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 20px;
}

.question-content {
  line-height: 1.8;
  color: #606266;
  font-size: 16px;
}

.tech-stack-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.tech-stack-section h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
}

.tech-stack-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.tech-card {
  display: flex;
  align-items: center;
  justify-content: center;
  aspect-ratio: 1 / 1;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
  word-break: break-word;
}

.tech-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tech-card.active {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.tech-name {
  font-size: 18px;
  color: #303133;
  text-align: center;
  line-height: 1.5;
  font-weight: 500;
}

.answer-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.answer-section h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 18px;
}

.selected-tech-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.selected-tech-section h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 18px;
}

.selected-tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>