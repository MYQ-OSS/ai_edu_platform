<!-- 代码雨效果组件 -->
<template>
  <canvas ref="canvasRef" class="code-rain-canvas" :style="{ zIndex }"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  zIndex: { type: Number, default: 0 },
  fontSize: { type: Number, default: 14 },
  speed: { type: Number, default: 1 },
  density: { type: Number, default: 0.8 }
})

const canvasRef = ref(null)
let animationId = null
let columns = []

const chars = 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン0123456789ABCDEF<>/{}[]();=+-*&^%$#@!'

function init() {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight

  const colCount = Math.floor(canvas.width / props.fontSize * props.density)
  columns = Array(colCount).fill(0).map(() => Math.random() * canvas.height)
}

function draw() {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')

  ctx.fillStyle = 'rgba(10, 14, 26, 0.05)'
  ctx.fillRect(0, 0, canvas.width, canvas.height)

  ctx.font = `${props.fontSize}px 'JetBrains Mono', monospace`

  columns.forEach((y, i) => {
    const x = i * props.fontSize / props.density
    const char = chars[Math.floor(Math.random() * chars.length)]

    const hue = Math.random() > 0.5 ? 190 : 275
    ctx.fillStyle = `hsla(${hue}, 100%, 65%, ${Math.random() * 0.6 + 0.2})`
    ctx.fillText(char, x, y)

    if (y > canvas.height && Math.random() > 0.98) {
      columns[i] = 0
    }
    columns[i] += props.fontSize * props.speed
  })

  animationId = requestAnimationFrame(draw)
}

function handleResize() {
  init()
}

onMounted(() => {
  init()
  draw()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.code-rain-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
</style>
