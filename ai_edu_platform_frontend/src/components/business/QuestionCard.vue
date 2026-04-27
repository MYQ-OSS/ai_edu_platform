<!-- 题目卡片组件 - Cyberpunk 2.0 -->
<template>
  <div class="question-card">
    <div class="card-glow"></div>
    <div class="card-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.question-card {
  position: relative;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  overflow: hidden;
  transition: all var(--transition-base);
}

.question-card:hover {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.2), 0 0 50px rgba(0, 212, 255, 0.05);
  transform: translateY(-2px);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-purple), transparent);
  opacity: 0.6;
}

.card-content {
  padding: 24px;
  position: relative;
  z-index: 1;
}
</style>
