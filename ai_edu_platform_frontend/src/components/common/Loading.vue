<!-- 加载组件 - Cyberpunk 2.0 -->
<template>
  <div class="loading-container">
    <div class="loading-spinner">
      <div class="spinner-ring"></div>
      <div class="spinner-ring inner"></div>
    </div>
    <span class="loading-text"><slot>加载中...</slot></span>
  </div>
</template>

<script setup>
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-spinner {
  position: relative;
  width: 48px;
  height: 48px;
}

.spinner-ring {
  position: absolute;
  inset: 0;
  border: 2px solid transparent;
  border-top-color: var(--neon-cyan);
  border-right-color: var(--neon-purple);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.spinner-ring.inner {
  inset: 6px;
  border-top-color: var(--neon-pink);
  border-right-color: transparent;
  border-left-color: var(--neon-cyan);
  animation-duration: 0.8s;
  animation-direction: reverse;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 13px;
  color: var(--text-muted);
  letter-spacing: 2px;
  animation: neon-pulse 2s ease-in-out infinite;
}
</style>
