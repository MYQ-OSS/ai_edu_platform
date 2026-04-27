<!-- 薪资评估表单组件 - Cyberpunk 2.0 -->
<template>
  <div class="salary-form">
    <slot></slot>
  </div>
</template>

<script setup>
</script>

<style scoped>
.salary-form {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  padding: 30px;
  animation: breathe 5s ease-in-out infinite;
  transition: all var(--transition-base);
}

.salary-form:hover {
  border-color: var(--neon-pink);
  box-shadow: 0 0 20px rgba(255, 0, 128, 0.15), 0 0 40px rgba(255, 0, 128, 0.05);
}
</style>
