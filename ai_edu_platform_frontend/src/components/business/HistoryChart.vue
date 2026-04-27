<!-- 答题历史图表组件 - Cyberpunk 2.0 -->
<template>
  <div class="history-chart">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const chartRef = ref(null);
</script>

<style scoped>
.history-chart {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  padding: 20px;
  transition: all var(--transition-base);
}

.history-chart:hover {
  border-color: var(--neon-purple);
  box-shadow: 0 0 20px rgba(180, 74, 255, 0.15), 0 0 40px rgba(180, 74, 255, 0.05);
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>
