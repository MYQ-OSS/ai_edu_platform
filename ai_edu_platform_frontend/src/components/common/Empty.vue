<!-- 空数据占位组件 - Cyberpunk 2.0 -->
<template>
  <div class="empty-placeholder">
    <div class="empty-icon">
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
        <rect x="8" y="16" width="48" height="36" rx="4" stroke="currentColor" stroke-width="2" fill="none"/>
        <line x1="16" y1="28" x2="48" y2="28" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <line x1="16" y1="36" x2="40" y2="36" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <line x1="16" y1="44" x2="32" y2="44" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
    <p class="empty-text"><slot>暂无数据</slot></p>
  </div>
</template>

<script setup>
</script>

<style scoped>
.empty-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--text-muted);
  animation: terminal-fade-in 0.6s ease-out;
}

.empty-icon {
  margin-bottom: 16px;
  opacity: 0.4;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.2));
}

.empty-text {
  font-size: 14px;
  letter-spacing: 1px;
  color: var(--text-muted);
  margin: 0;
}
</style>
