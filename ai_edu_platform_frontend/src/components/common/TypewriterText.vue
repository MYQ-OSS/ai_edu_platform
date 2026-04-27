<!-- 打字机效果组件 - 优化版 -->
<template>
  <span class="typewriter">
    <span>{{ displayText }}</span>
    <span v-if="isTyping" class="cursor">|</span>
  </span>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'

const props = defineProps({
  text: { type: String, required: true },
  speed: { type: Number, default: 80 },
  delay: { type: Number, default: 500 },
  loop: { type: Boolean, default: false }
})

const displayText = ref('')
const isTyping = ref(true)
let timer = null
let interval = null

function typeText() {
  // 清理之前的定时器
  if (interval) clearInterval(interval)
  if (timer) clearTimeout(timer)

  displayText.value = ''
  isTyping.value = true
  let index = 0

  timer = setTimeout(() => {
    interval = setInterval(() => {
      if (index < props.text.length) {
        displayText.value += props.text[index]
        index++
      } else {
        clearInterval(interval)
        isTyping.value = false
      }
    }, props.speed)
  }, props.delay)
}

onMounted(() => {
  typeText()
})

watch(() => props.text, () => {
  typeText()
}, { immediate: false })

// 组件卸载时清理定时器
onMounted(() => {
  return () => {
    if (interval) clearInterval(interval)
    if (timer) clearTimeout(timer)
  }
})
</script>

<style scoped>
.typewriter .cursor {
  animation: cursor-blink 1s infinite;
  color: var(--neon-cyan);
  font-weight: 300;
  will-change: transform; /* 优化渲染性能 */
}
</style>