<!-- 学习统计 - Cyberpunk 2.0 -->
<template>
  <div class="learning-statistics-container">
    <ParticleBackground :zIndex="0" :particleCount="15" particleColor="cyan" :speed="0.2" :interactive="false" />
    <h2 class="page-title gradient-text">学习统计</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-cyan">
          <div class="stat-content">
            <div class="stat-icon total">&#128202;</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalQuizCount || 0 }}</div>
              <div class="stat-label">总答题次数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-purple">
          <div class="stat-content">
            <div class="stat-icon average">&#127919;</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageScore || 0 }}</div>
              <div class="stat-label">平均得分</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-pink">
          <div class="stat-content">
            <div class="stat-icon accuracy">&#9989;</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageAccuracy || 0 }}%</div>
              <div class="stat-label">平均正确率</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card-blue">
          <div class="stat-content">
            <div class="stat-icon range">&#128200;</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.minScore || 0 }} - {{ statistics.maxScore || 0 }}</div>
              <div class="stat-label">得分区间</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card chart-card-gradient">
          <template #header>
            <div class="card-header gradient-text">得分趋势图</div>
          </template>
          <div ref="scoreChartRef" class="chart-container" v-loading="chartLoading"></div>
        </el-card>
      </el-col>

      <el-col :span="24">
        <el-card shadow="hover" class="chart-card chart-card-gradient">
          <template #header>
            <div class="card-header gradient-text">正确率趋势图</div>
          </template>
          <div ref="accuracyChartRef" class="chart-container" v-loading="chartLoading"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 返回按钮 -->
    <div class="back-button-section">
      <el-button @click="goBack" size="large" class="btn-back">
        <el-icon><Back /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getLearningStatistics } from '../../api/statisticsApi'
import ParticleBackground from '../../components/common/ParticleBackground.vue'

defineOptions({ name: 'LearningStatistics' })

const router = useRouter()

const statistics = ref({
  totalQuizCount: 0,
  averageScore: 0,
  averageAccuracy: 0,
  maxScore: 0,
  minScore: 0,
  scoreTrend: [],
  accuracyTrend: []
})

const chartLoading = ref(false)
const scoreChartRef = ref(null)
const accuracyChartRef = ref(null)
let scoreChart = null
let accuracyChart = null
let resizeTimer = null

const loadStatistics = async () => {
  chartLoading.value = true
  try {
    const res = await getLearningStatistics()
    if (res.code === 200) {
      statistics.value = res.data
      // 使用 requestAnimationFrame 确保在下一帧渲染图表，避免阻塞
      requestAnimationFrame(() => {
        nextTick(() => {
          drawCharts()
        })
      })
    } else {
      ElMessage.error(res.msg || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    // 延迟关闭loading，让图表有时间渲染
    setTimeout(() => {
      chartLoading.value = false
    }, 100)
  }
}

const drawCharts = () => {
  drawScoreChart()
  drawAccuracyChart()
}

const drawScoreChart = () => {
  if (!scoreChartRef.value || !statistics.value.scoreTrend.length) return

  // 销毁旧图表实例
  if (scoreChart) {
    scoreChart.dispose()
  }

  scoreChart = echarts.init(scoreChartRef.value)
  const data = statistics.value.scoreTrend

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(20, 27, 51, 0.95)',
      borderColor: 'rgba(0, 212, 255, 0.3)',
      textStyle: { color: '#e8eaf0', fontFamily: 'JetBrains Mono, monospace' },
      formatter: (params) => {
        const item = params[0]
        return `${item.name}<br/>题目：${data[item.dataIndex].questionName}<br/>得分：${item.value}`
      }
    },
    animation: true,
    animationDuration: 800,
    animationEasing: 'cubicOut',
    grid: {
      left: '3%', right: '4%', bottom: '15%', containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.quizTime),
      axisLabel: { rotate: 45, interval: Math.ceil(data.length / 10), color: '#8a94b8', fontFamily: 'JetBrains Mono, monospace' },
      axisLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.2)' } }
    },
    yAxis: {
      type: 'value', name: '得分', min: 0, max: 100,
      axisLabel: { color: '#8a94b8', fontFamily: 'JetBrains Mono, monospace' },
      splitLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.06)' } },
      axisLine: { lineStyle: { color: 'rgba(0, 212, 255, 0.2)' } }
    },
    series: [
      {
        name: '得分', type: 'line', data: data.map(item => item.score), smooth: true,
        lineStyle: { color: '#00d4ff', width: 3 },
        itemStyle: { color: '#00d4ff' },
        showSymbol: false,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0, 212, 255, 0.3)' },
            { offset: 1, color: 'rgba(180, 74, 255, 0.05)' }
          ])
        },
        markPoint: {
          symbolSize: 40,
          data: [
            { type: 'max', name: '最高分', itemStyle: { color: '#00d4ff' } },
            { type: 'min', name: '最低分', itemStyle: { color: '#ff0080' } }
          ]
        },
        markLine: {
          symbol: 'none',
          data: [{ type: 'average', name: '平均分', lineStyle: { color: '#ffee00', type: 'dashed' } }]
        }
      }
    ]
  }

  scoreChart.setOption(option)
}

const drawAccuracyChart = () => {
  if (!accuracyChartRef.value || !statistics.value.accuracyTrend.length) return

  // 销毁旧图表实例
  if (accuracyChart) {
    accuracyChart.dispose()
  }

  accuracyChart = echarts.init(accuracyChartRef.value)
  const data = statistics.value.accuracyTrend

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(20, 27, 51, 0.95)',
      borderColor: 'rgba(180, 74, 255, 0.3)',
      textStyle: { color: '#e8eaf0', fontFamily: 'JetBrains Mono, monospace' },
      formatter: (params) => {
        const item = params[0]
        return `${item.name}<br/>题目：${data[item.dataIndex].questionName}<br/>正确率：${item.value}%`
      }
    },
    animation: true,
    animationDuration: 800,
    animationEasing: 'cubicOut',
    grid: {
      left: '3%', right: '4%', bottom: '15%', containLabel: true
    },
    xAxis: {
      type: 'category', data: data.map(item => item.quizTime),
      axisLabel: { rotate: 45, interval: Math.ceil(data.length / 10), color: '#8a94b8', fontFamily: 'JetBrains Mono, monospace' },
      axisLine: { lineStyle: { color: 'rgba(180, 74, 255, 0.2)' } }
    },
    yAxis: {
      type: 'value', name: '正确率(%)', min: 0, max: 100,
      axisLabel: { color: '#8a94b8', fontFamily: 'JetBrains Mono, monospace' },
      splitLine: { lineStyle: { color: 'rgba(180, 74, 255, 0.06)' } },
      axisLine: { lineStyle: { color: 'rgba(180, 74, 255, 0.2)' } }
    },
    series: [
      {
        name: '正确率', type: 'line', data: data.map(item => item.accuracy), smooth: true,
        lineStyle: { color: '#b44aff', width: 3 },
        itemStyle: { color: '#b44aff' },
        showSymbol: false,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(180, 74, 255, 0.3)' },
            { offset: 1, color: 'rgba(255, 0, 128, 0.05)' }
          ])
        },
        markPoint: {
          symbolSize: 40,
          data: [
            { type: 'max', name: '最高正确率', itemStyle: { color: '#b44aff' } },
            { type: 'min', name: '最低正确率', itemStyle: { color: '#ff0080' } }
          ]
        },
        markLine: {
          symbol: 'none',
          data: [{ type: 'average', name: '平均正确率', lineStyle: { color: '#ffee00', type: 'dashed' } }]
        }
      }
    ]
  }

  accuracyChart.setOption(option)
}

const goBack = () => {
  router.push('/home')
}

// 处理窗口resize（防抖）
const handleResize = () => {
  if (resizeTimer) clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    if (scoreChart) scoreChart.resize()
    if (accuracyChart) accuracyChart.resize()
  }, 200)
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  // 清理事件监听器
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例，释放内存
  if (scoreChart) {
    scoreChart.dispose()
    scoreChart = null
  }
  if (accuracyChart) {
    accuracyChart.dispose()
    accuracyChart = null
  }
  
  // 清除定时器
  if (resizeTimer) {
    clearTimeout(resizeTimer)
  }
})
</script>

<style scoped>
.learning-statistics-container {
  padding: 30px 40px;
  max-width: 1600px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
  position: relative;
}

.page-title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 30px;
  font-weight: 900;
  letter-spacing: 3px;
  position: relative;
  z-index: 1;
}

.statistics-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
  transition: all var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-4px);
  border-color: var(--panel-border-active);
  box-shadow: var(--panel-glow-active);
}

.stat-card :deep(.el-card__body) {
  padding: 16px;
}

.stat-card-cyan { --accent: var(--neon-cyan); --glow: var(--glow-cyan); border-color: rgba(0, 212, 255, 0.2); }
.stat-card-purple { --accent: var(--neon-purple); --glow: var(--glow-purple); border-color: rgba(180, 74, 255, 0.2); }
.stat-card-pink { --accent: var(--neon-pink); --glow: var(--glow-pink); border-color: rgba(255, 0, 128, 0.2); }
.stat-card-blue { --accent: var(--neon-blue); --glow: var(--glow-blue); border-color: rgba(74, 158, 255, 0.2); }

.stat-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  transition: transform var(--transition-base);
  font-weight: 800;
}

.stat-icon.total {
  background: rgba(0, 212, 255, 0.1);
  color: var(--neon-cyan);
  border: 1px solid rgba(0, 212, 255, 0.3);
}
.stat-icon.average {
  background: rgba(180, 74, 255, 0.1);
  color: var(--neon-purple);
  border: 1px solid rgba(180, 74, 255, 0.3);
}
.stat-icon.accuracy {
  background: rgba(255, 0, 128, 0.1);
  color: var(--neon-pink);
  border: 1px solid rgba(255, 0, 128, 0.3);
}
.stat-icon.range {
  background: rgba(74, 158, 255, 0.1);
  color: var(--neon-blue);
  border: 1px solid rgba(74, 158, 255, 0.3);
}

.stat-card:hover .stat-icon {
  transform: scale(1.15);
}

.stat-info { flex: 1; }

.stat-value {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 4px;
}

.stat-card-cyan .stat-value { color: var(--neon-cyan); text-shadow: var(--glow-cyan); }
.stat-card-purple .stat-value { color: var(--neon-purple); text-shadow: var(--glow-text-purple); }
.stat-card-pink .stat-value { color: var(--neon-pink); text-shadow: var(--glow-text-green); }
.stat-card-blue .stat-value { color: var(--neon-blue); text-shadow: var(--glow-blue); }

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  letter-spacing: 1px;
}

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  margin-bottom: 16px;
  transition: all var(--transition-base);
}

.chart-card:hover {
  border-color: var(--panel-border-active);
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.15), 0 0 60px rgba(180, 74, 255, 0.08);
}

.chart-card-gradient :deep(.el-card__header) {
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.06), rgba(180, 74, 255, 0.06));
  border-bottom: 1px solid var(--divider);
}

.card-header {
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 1px;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--divider);
}

.btn-back {
  background: transparent !important;
  border: 1px solid var(--neon-cyan) !important;
  color: var(--neon-cyan) !important;
}

.btn-back:hover {
  background: rgba(0, 212, 255, 0.1) !important;
  box-shadow: var(--glow-cyan) !important;
}

@media (max-width: 1200px) {
  .learning-statistics-container { padding: 20px 30px; }
}

@media (max-width: 768px) {
  .learning-statistics-container { padding: 15px 20px; }
  .page-title { font-size: 22px; margin-bottom: 16px; }
  .stat-card { margin-bottom: 12px; }
  .chart-container { height: 280px; }
}

@media (max-width: 480px) {
  .learning-statistics-container { padding: 10px 15px; }
  .page-title { font-size: 18px; }
  .stat-value { font-size: 18px; }
  .chart-container { height: 220px; }
}
</style>
