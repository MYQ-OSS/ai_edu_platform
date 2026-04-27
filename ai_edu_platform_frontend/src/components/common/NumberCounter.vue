<!-- 数字滚动动画组件 - 优化版 -->
<template>
  <span class="number-counter" :class="{ 'digit-animate': animated }">{{ displayValue }}</span>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  target: { type: Number, required: true },
  duration: { type: Number, default: 1500 },
  decimals: { type: Number, default: 0 },
  prefix: { type: String, default: '' },
  suffix: { type: String, default: '' }
})

const displayValue = ref(props.prefix + '0' + props.suffix)
const animated = ref(false)
let animationId = null

function animateNumber() {
  // 清理之前的动画
  if (animationId) cancelAnimationFrame(animationId)

  animated.value = true
  const start = parseFloat(displayValue.value.replace(/[^\d.-]/g, '')) || 0
  const end = props.target
  const startTime = performance.now()

  function update(currentTime) {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / props.duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    const current = start + (end - start) * eased

    // 使用整数值减少重绘
    const newValue = props.prefix + current.toFixed(props.decimals) + props.suffix
    if (newValue !== displayValue.value) {
      displayValue.value = newValue
    }

    if (progress < 1) {
      animationId = requestAnimationFrame(update)
    }
  }
  animationId = requestAnimationFrame(update)
}

onMounted(() => {
  animateNumber()
})

watch(() => props.target, () => {
  animateNumber()
})

// 组件卸载时清理动画
onMounted(() => {
  return () => {
    if (animationId) cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.number-counter {
  display: inline-block;
  transition: all 0.3s ease;
  will-change: transform; /* 优化渲染性能 */
}
</style>