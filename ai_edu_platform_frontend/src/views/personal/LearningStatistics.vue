<template>
  <div class="learning-statistics-container">
    <h2>学习统计</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">📊</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalQuizCount || 0 }}</div>
              <div class="stat-label">总答题次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon average">🎯</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageScore || 0 }}</div>
              <div class="stat-label">平均得分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon accuracy">✅</div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageAccuracy || 0 }}%</div>
              <div class="stat-label">平均正确率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon range">📈</div>
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
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>得分趋势图</span>
            </div>
          </template>
          <div ref="scoreChartRef" class="chart-container" v-loading="chartLoading"></div>
        </el-card>
      </el-col>
      
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>正确率趋势图</span>
            </div>
          </template>
          <div ref="accuracyChartRef" class="chart-container" v-loading="chartLoading"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 返回按钮 -->
    <div class="back-button-section">
      <el-button @click="goBack" size="large">
        <el-icon><Back /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getLearningStatistics } from '../../api/statisticsApi'

const router = useRouter()

// 统计数据
const statistics = ref({
  totalQuizCount: 0,
  averageScore: 0,
  averageAccuracy: 0,
  maxScore: 0,
  minScore: 0,
  scoreTrend: [],
  accuracyTrend: []
})

// 图表加载状态
const chartLoading = ref(false)

// 图表引用
const scoreChartRef = ref(null)
const accuracyChartRef = ref(null)

/**
 * 加载统计数据
 */
const loadStatistics = async () => {
  chartLoading.value = true
  try {
    const res = await getLearningStatistics()
    if (res.code === 200) {
      statistics.value = res.data
      // 数据加载完成后绘制图表
      await nextTick()
      drawCharts()
    } else {
      ElMessage.error(res.msg || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    chartLoading.value = false
  }
}

/**
 * 绘制图表
 */
const drawCharts = () => {
  drawScoreChart()
  drawAccuracyChart()
}

/**
 * 绘制得分趋势图
 */
const drawScoreChart = () => {
  if (!scoreChartRef.value || !statistics.value.scoreTrend.length) return
  
  const chart = echarts.init(scoreChartRef.value)
  const data = statistics.value.scoreTrend
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const item = params[0]
        return `${item.name}<br/>题目：${data[item.dataIndex].questionName}<br/>得分：${item.value}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.quizTime),
      axisLabel: {
        rotate: 45,
        interval: Math.ceil(data.length / 10) // 根据数据量动态调整显示间隔
      }
    },
    yAxis: {
      type: 'value',
      name: '得分',
      min: 0,
      max: 100
    },
    series: [
      {
        name: '得分',
        type: 'line',
        data: data.map(item => item.score),
        smooth: true,
        lineStyle: {
          color: '#409EFF',
          width: 3
        },
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        markPoint: {
          data: [
            { type: 'max', name: '最高分' },
            { type: 'min', name: '最低分' }
          ]
        },
        markLine: {
          data: [
            { type: 'average', name: '平均分' }
          ]
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

/**
 * 绘制正确率趋势图
 */
const drawAccuracyChart = () => {
  if (!accuracyChartRef.value || !statistics.value.accuracyTrend.length) return
  
  const chart = echarts.init(accuracyChartRef.value)
  const data = statistics.value.accuracyTrend
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const item = params[0]
        return `${item.name}<br/>题目：${data[item.dataIndex].questionName}<br/>正确率：${item.value}%`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.quizTime),
      axisLabel: {
        rotate: 45,
        interval: Math.ceil(data.length / 10)
      }
    },
    yAxis: {
      type: 'value',
      name: '正确率(%)',
      min: 0,
      max: 100
    },
    series: [
      {
        name: '正确率',
        type: 'line',
        data: data.map(item => item.accuracy),
        smooth: true,
        lineStyle: {
          color: '#67C23A',
          width: 3
        },
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        markPoint: {
          data: [
            { type: 'max', name: '最高正确率' },
            { type: 'min', name: '最低正确率' }
          ]
        },
        markLine: {
          data: [
            { type: 'average', name: '平均正确率' }
          ]
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

/**
 * 返回首页
 */
const goBack = () => {
  router.push('/home')
}

// 页面加载时初始化
onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.learning-statistics-container {
  padding: 30px 50px;
  max-width: 1600px;
  margin: 0 auto;
}

.learning-statistics-container h2 {
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
  font-size: 28px;
  font-weight: 600;
}

.statistics-cards {
  margin-bottom: 30px;
}

.stat-card {
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.average {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.accuracy {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.range {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .learning-statistics-container {
    padding: 20px 30px;
  }
}

@media (max-width: 768px) {
  .learning-statistics-container {
    padding: 15px 20px;
  }

  .learning-statistics-container h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }

  .statistics-cards {
    margin-bottom: 20px;
  }

  .stat-card {
    margin-bottom: 15px;
  }

  .chart-container {
    height: 300px;
  }
}

@media (max-width: 480px) {
  .learning-statistics-container {
    padding: 10px 15px;
  }

  .learning-statistics-container h2 {
    font-size: 20px;
    margin-bottom: 15px;
  }

  .stat-value {
    font-size: 20px;
  }

  .chart-container {
    height: 250px;
  }

  .back-button-section :deep(.el-button) {
    width: 100%;
  }
}
</style>
