<!-- 答题详情 - Cyberpunk 2.0 -->
<template>
  <div class="answer-detail-container">
    <h2 class="page-title gradient-text">答题详情</h2>
    <div class="detail-card">
      <div class="detail-section">
        <!-- 核心数据展示 -->
        <div class="score-overview">
          <div class="score-item">
            <div class="score-label">得分</div>
            <div class="score-value">{{ answerDetail.score }}</div>
          </div>
          <div class="score-divider"></div>
          <div class="score-item">
            <div class="score-label">正确率</div>
            <div class="score-value accuracy">{{ answerDetail.accuracy }}%</div>
          </div>
          <div class="score-divider"></div>
          <div class="score-item">
            <div class="score-label">答题时间</div>
            <div class="score-value time">{{ answerDetail.createTime }}</div>
          </div>
        </div>

        <!-- 用户答案 -->
        <div class="detail-item full-width" v-if="answerDetail.userOptions">
          <label class="detail-label">您的选项：</label>
          <div class="detail-text user-options">{{ formatOptions(answerDetail.userOptions) }}</div>
        </div>
        <div class="detail-item full-width" v-if="answerDetail.userAnswer">
          <label class="detail-label">您的文本答案：</label>
          <div class="detail-text user-answer">{{ answerDetail.userAnswer }}</div>
        </div>

        <!-- 正确选项 -->
        <div class="detail-item full-width" v-if="answerDetail.trueOptions">
          <label class="detail-label">正确选项：</label>
          <div class="detail-text true-options">{{ formatOptions(answerDetail.trueOptions) }}</div>
        </div>

        <!-- AI评价 -->
        <div class="detail-item full-width" v-if="answerDetail.comment">
          <label class="detail-label">AI评价：</label>
          <div class="detail-text comment">{{ answerDetail.comment }}</div>
        </div>

        <!-- 评分原因 -->
        <div class="detail-item full-width" v-if="answerDetail.reason">
          <label class="detail-label">评分原因：</label>
          <div class="detail-text reason">{{ answerDetail.reason }}</div>
        </div>

        <!-- 题目解析 -->
        <div class="detail-item full-width" v-if="answerDetail.analysis">
          <label class="detail-label">题目解析：</label>
          <div class="detail-text analysis">{{ answerDetail.analysis }}</div>
        </div>

        <!-- 学习建议 -->
        <div class="detail-item full-width" v-if="answerDetail.suggest">
          <label class="detail-label">学习建议：</label>
          <div class="detail-text suggest">{{ answerDetail.suggest }}</div>
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
import { getQuizReport } from '../../api/questionApi'

const router = useRouter()
const route = useRoute()
const recordId = route.params.id

const answerDetail = reactive({
  recordId: '', questionId: '', userOptions: '', userAnswer: '', score: 0, comment: '',
  suggest: '', reason: '', trueOptions: '', analysis: '', accuracy: 0, createTime: ''
})

const loading = ref(false)

onMounted(async () => {
  await loadAnswerDetail()
})

const loadAnswerDetail = async () => {
  if (!recordId) {
    ElMessage.error('缺少答题记录ID')
    return
  }
  loading.value = true
  try {
    const response = await getQuizReport(recordId)
    if (response.code === 200) {
      Object.assign(answerDetail, response.data)
    } else {
      ElMessage.error(response.msg || '获取答题详情失败')
      router.push('/personal/info')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取答题详情失败，请稍后重试')
    router.push('/personal/info')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/personal/info')
}

const formatOptions = (optionsJson) => {
  if (!optionsJson) return ''
  try {
    const options = JSON.parse(optionsJson)
    if (Array.isArray(options)) {
      return options.map((opt, index) => {
        let text = ''
        if (typeof opt === 'object') {
          text = opt.label || opt.value || JSON.stringify(opt)
        } else {
          text = String(opt)
        }
        return `${index + 1}. ${text}`
      }).join('\n')
    }
    return optionsJson
  } catch (e) {
    return optionsJson
  }
}
</script>

<style scoped>
.answer-detail-container {
  padding: 30px 40px;
  max-width: 1400px;
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

.detail-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  padding: 32px;
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  animation: breathe 5s ease-in-out infinite;
}

/* 分数概览 */
.score-overview {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 28px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.04), rgba(180, 74, 255, 0.04));
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-md);
  margin-bottom: 24px;
}

.score-item { text-align: center; flex: 1; }

.score-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.score-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--neon-cyan);
  line-height: 1.2;
  text-shadow: var(--glow-text-cyan);
}

.score-value.accuracy {
  color: var(--neon-purple);
  text-shadow: var(--glow-text-purple);
}

.score-value.time {
  font-size: 14px;
  color: var(--text-secondary);
}

.score-divider {
  width: 1px;
  height: 40px;
  background: linear-gradient(180deg, transparent, var(--divider-strong), transparent);
}

/* 详情区块 */
.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(0, 212, 255, 0.03);
  border-radius: var(--radius-xs);
  border: 1px solid var(--divider);
}

.detail-item.full-width {
  flex-direction: column;
}

.detail-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--neon-cyan);
  min-width: 80px;
  margin-right: 12px;
  line-height: 1.8;
}

.detail-text {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-primary);
  width: 100%;
  margin-top: 8px;
  padding: 14px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: var(--radius-xs);
  border-left: 3px solid var(--neon-cyan);
}

.detail-text.user-options {
  white-space: pre-line;
  border-left-color: var(--text-muted);
}

.detail-text.user-answer { border-left-color: var(--neon-green); }

.detail-text.true-options {
  white-space: pre-line;
  line-height: 2;
  border-left-color: var(--neon-green);
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
}

.detail-text.comment { border-left-color: var(--neon-yellow); }
.detail-text.reason { border-left-color: var(--neon-pink); }
.detail-text.analysis { border-left-color: var(--neon-cyan); }
.detail-text.suggest { border-left-color: var(--neon-blue); }

.button-section {
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid var(--divider);
}

@media (max-width: 768px) {
  .answer-detail-container { padding: 20px; }
  .detail-card { padding: 24px; }
}
</style>
