<!-- 动态粒子背景组件 - 性能优化版 -->
<template>
  <canvas ref="canvasRef" class="particle-canvas" :style="{ zIndex }"></canvas>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  zIndex: { type: Number, default: 0 },
  particleCount: { type: Number, default: 20 }, // 进一步减少到20个粒子
  particleColor: { type: String, default: 'cyan' },
  speed: { type: Number, default: 0.15 }, // 进一步降低速度
  interactive: { type: Boolean, default: false }, // 完全关闭交互
  lineWidth: { type: Number, default: 0.3 },
  connectionDistance: { type: Number, default: 100 },
  mode: { type: String, default: 'particles' }
})

const canvasRef = ref(null)
let particles = []
let animationId = null
let ctx = null
let lastTime = 0

const colorMap = {
  cyan: { r: 0, g: 212, b: 255 },
  purple: { r: 180, g: 74, b: 255 },
  green: { r: 0, g: 255, b: 65 },
  pink: { r: 255, g: 0, b: 128 },
  mixed: null
}

function getRandomColor() {
  if (props.particleColor === 'mixed') {
    const colors = [
      { r: 0, g: 212, b: 255 },
      { r: 180, g: 74, b: 255 },
      { r: 255, g: 0, b: 128 },
      { r: 0, g: 255, b: 65 }
    ]
    return colors[Math.floor(Math.random() * colors.length)]
  }
  return colorMap[props.particleColor] || colorMap.cyan
}

class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.x = Math.random() * canvas.width
    this.y = Math.random() * canvas.height
    this.vx = (Math.random() - 0.5) * props.speed * 2
    this.vy = (Math.random() - 0.5) * props.speed * 2
    this.radius = Math.random() * 1.2 + 0.3 // 减小粒子大小
    this.color = getRandomColor()
    this.opacity = Math.random() * 0.3 + 0.1 // 降低透明度
  }

  update() {
    this.x += this.vx
    this.y += this.vy

    // 边界反弹
    if (this.x < 0 || this.x > this.canvas.width) this.vx *= -1
    if (this.y < 0 || this.y > this.canvas.height) this.vy *= -1
  }

  draw(ctx) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(${this.color.r}, ${this.color.g}, ${this.color.b}, ${this.opacity})`
    ctx.fill()
  }
}

function initParticles() {
  const canvas = canvasRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight

  particles = []
  for (let i = 0; i < props.particleCount; i++) {
    particles.push(new Particle(canvas))
  }
}

function drawConnections() {
  if (props.mode !== 'particles') return

  // 优化连接线绘制：只绘制必要的连接
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const dist = Math.sqrt(dx * dx + dy * dy)

      if (dist < props.connectionDistance) {
        const opacity = (1 - dist / props.connectionDistance) * 0.15
        const c1 = particles[i].color
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.strokeStyle = `rgba(${c1.r}, ${c1.g}, ${c1.b}, ${opacity})`
        ctx.lineWidth = props.lineWidth * 0.5
        ctx.stroke()
      }
    }
  }
}

function animate(currentTime) {
  if (!ctx || !canvasRef.value) return

  // 节流控制帧率
  if (currentTime - lastTime < 40) { // 约25fps
    animationId = requestAnimationFrame(animate)
    return
  }
  lastTime = currentTime

  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)

  particles.forEach(p => {
    p.update()
    p.draw(ctx)
  })

  drawConnections()
  animationId = requestAnimationFrame(animate)
}

function handleResize() {
  if (!canvasRef.value) return
  const canvas = canvasRef.value
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  // 重新初始化粒子位置
  particles.forEach(p => {
    p.x = Math.random() * canvas.width
    p.y = Math.random() * canvas.height
  })
}

onMounted(() => {
  initParticles()
  animate(0)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  will-change: transform;
}
</style>