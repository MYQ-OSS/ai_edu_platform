<!-- 答题历史图表组件 -->
<template>
    <div class="history-chart">
        <div ref="chartRef"></div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const chartRef = ref(null);
</script>

<style scoped>
</style>
