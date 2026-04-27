<!-- 答题历史页面 - Cyberpunk 2.0 -->
<template>
  <div class="answer-history">
    <h2 class="page-title gradient-text">答题历史</h2>
    <div class="history-card">
      <p>答题历史内容已整合至 PersonalInfo.vue 的标签页中</p>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.answer-history {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
  animation: terminal-fade-in 0.8s ease-out;
}

.page-title {
  margin-bottom: 30px;
  text-align: center;
  font-size: 28px;
  font-weight: 900;
  letter-spacing: 2px;
}

.history-card {
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow-active);
  padding: 40px;
  text-align: center;
  animation: breathe 5s ease-in-out infinite;
}

.history-card p {
  color: var(--text-secondary);
  font-size: 16px;
}

@media (max-width: 768px) {
  .answer-history { padding: 20px; }
  .page-title { font-size: 24px; }
}
</style>
