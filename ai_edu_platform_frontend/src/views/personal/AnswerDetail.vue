<!-- 答题历史详情页 -->
<template>
  <div class="answer-detail-container">
    <h2>答题详情</h2>
    <div class="detail-card">
      <el-form :model="answerDetail" label-width="120px">
        <el-form-item label="题目ID">
          <el-input v-model="answerDetail.id" disabled />
        </el-form-item>
        <el-form-item label="题目类型">
          <el-input v-model="answerDetail.question_type" disabled />
        </el-form-item>
        <el-form-item label="题目内容">
          <el-input v-model="answerDetail.question_content" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="您的答案">
          <el-input v-model="answerDetail.user_answer" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="answerDetail.correct_answer" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="得分">
          <el-input v-model="answerDetail.score" disabled />
        </el-form-item>
        <el-form-item label="答题时间">
          <el-input v-model="answerDetail.created_at" disabled />
        </el-form-item>
        <el-form-item label="答题用时">
          <el-input v-model="answerDetail.time_used" disabled />
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
const answerId = route.params.id

const answerDetail = reactive({
  id: '',
  question_type: '',
  question_content: '',
  user_answer: '',
  correct_answer: '',
  score: '',
  created_at: '',
  time_used: ''
})

// 页面加载时获取答题详情
onMounted(async () => {
  await loadAnswerDetail()
})

const loadAnswerDetail = async () => {
  try {
    // 模拟获取答题详情数据
    // 实际项目中应该调用API获取真实数据
    const mockData = {
      1: {
        id: '1',
        question_type: '技术挑战题',
        question_content: '请简述Vue3的Composition API与Options API的区别，并说明在什么场景下使用Composition API更为合适。',
        user_answer: 'Vue3的Composition API与Options API的主要区别在于代码组织方式。Options API将相关代码分散在不同的选项中，而Composition API允许我们将相关代码组织在一起。在复杂组件中，使用Composition API可以更好地组织代码，提高代码的可维护性和复用性。',
        correct_answer: 'Vue3的Composition API与Options API的主要区别在于代码组织方式和逻辑复用。Options API将相关代码分散在不同的选项中（如data、methods、computed等），而Composition API允许我们将相关代码组织在一起，形成逻辑关注点。在以下场景下使用Composition API更为合适：1. 复杂组件，需要更好地组织代码；2. 需要逻辑复用的场景；3. 类型推导更为友好，适合TypeScript项目。',
        score: '85',
        created_at: '2026-03-28 14:30:00',
        time_used: '15分钟'
      },
      2: {
        id: '2',
        question_type: '基础知识题',
        question_content: '请解释JavaScript中的闭包概念，并给出一个实际应用场景。',
        user_answer: '闭包是指函数能够访问其词法作用域之外的变量。即使函数在其词法作用域之外被调用，它仍然能够访问这些变量。实际应用场景包括：模块化编程、防抖和节流函数、事件处理等。',
        correct_answer: '闭包是指函数能够访问其词法作用域之外的变量。即使函数在其词法作用域之外被调用，它仍然能够访问这些变量。实际应用场景包括：1. 模块化编程，创建私有变量和方法；2. 防抖和节流函数，保存状态；3. 事件处理，保存事件处理函数的上下文；4. 函数工厂，创建具有特定配置的函数。',
        score: '92',
        created_at: '2026-03-25 10:15:00',
        time_used: '8分钟'
      },
      3: {
        id: '3',
        question_type: '技术挑战题',
        question_content: '请设计一个基于React的组件，实现一个可拖拽的待办事项列表。',
        user_answer: '我会使用React的useState钩子来管理待办事项列表，使用HTML5的拖拽API来实现拖拽功能。具体来说，我会为每个待办事项添加draggable属性，然后实现onDragStart、onDragOver和onDrop事件处理函数来完成拖拽逻辑。',
        correct_answer: '设计一个基于React的可拖拽待办事项列表，需要以下步骤：1. 使用useState钩子管理待办事项列表和拖拽状态；2. 为每个待办事项添加draggable属性；3. 实现onDragStart事件处理函数，记录被拖拽的元素索引；4. 实现onDragOver事件处理函数，阻止默认行为；5. 实现onDrop事件处理函数，更新待办事项列表的顺序；6. 添加适当的样式，提升用户体验。',
        score: '78',
        created_at: '2026-03-20 16:45:00',
        time_used: '20分钟'
      }
    }
    
    if (mockData[answerId]) {
      Object.assign(answerDetail, mockData[answerId])
    } else {
      ElMessage.error('未找到答题详情')
    }
  } catch (error) {
    ElMessage.error('获取答题详情失败')
  }
}

const goBack = () => {
  router.push('/personal/info')
}
</script>

<style scoped>
.answer-detail-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.answer-detail-container h2 {
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