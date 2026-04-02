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
            <div class="tech-icon">{{ tech.icon }}</div>
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

const router = useRouter()
const loading = ref(false)
const answer = ref('')
const selectedTechStacks = ref([])

// 模拟题目数据
const question = reactive({
  title: '如何设计一个高性能的前端应用？',
  content: '请设计一个高性能的前端应用，考虑以下因素：1. 页面加载速度；2. 运行时性能；3. 代码可维护性；4. 用户体验。请结合具体的技术栈和最佳实践，详细描述你的设计方案。'
})

// 模拟技术栈数据
const techStacks = reactive([
  { id: 1, name: 'Vue 3', icon: '🟢' },
  { id: 2, name: 'React', icon: '⚛️' },
  { id: 3, name: 'TypeScript', icon: '🔷' },
  { id: 4, name: 'Webpack', icon: '📦' },
  { id: 5, name: 'Vite', icon: '⚡' },
  { id: 6, name: 'Tailwind CSS', icon: '🎨' },
  { id: 7, name: 'Redux', icon: '🔄' },
  { id: 8, name: 'GraphQL', icon: '🔗' }
])

// 页面加载时获取前提信息
onMounted(() => {
  const inputData = localStorage.getItem('questionInputData')
  if (!inputData) {
    ElMessage.warning('请先填写前提信息')
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
  const tech = techStacks.find(t => t.id === techId)
  return tech ? tech.name : ''
}

// 提交答案
const handleSubmit = async () => {
  if (!answer.value) {
    ElMessage.warning('请输入答案')
    return
  }
  
  if (selectedTechStacks.value.length === 0) {
    ElMessage.warning('请至少选择一个技术栈')
    return
  }
  
  loading.value = true
  try {
    // 模拟提交答案
    // 实际项目中应该调用API提交答案
    setTimeout(() => {
      loading.value = false
      // 保存答案数据到localStorage，以便在报告页使用
      localStorage.setItem('questionAnswerData', JSON.stringify({
        answer: answer.value,
        selectedTechStacks: selectedTechStacks.value
      }))
      ElMessage.success('答案提交成功')
      router.push('/question/report')
    }, 1000)
  } catch (error) {
    loading.value = false
    ElMessage.error('提交失败，请稍后重试')
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
  max-width: 1000px;
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
  margin-bottom: 10px;
  color: #303133;
}

.question-content {
  line-height: 1.6;
  color: #606266;
}

.tech-stack-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.tech-stack-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.tech-stack-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.tech-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
}

.tech-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tech-card.active {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.tech-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.tech-name {
  font-size: 14px;
  color: #303133;
  text-align: center;
}

.answer-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.answer-section h3 {
  margin-bottom: 10px;
  color: #303133;
}

.selected-tech-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.selected-tech-section h3 {
  margin-bottom: 10px;
  color: #303133;
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