<!-- 答题历史页面 -->
<template>
    <div class="answer-history">
        <h2>个人中心 - 答题历史</h2>
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
  animation: tech-fade-in 0.6s ease-out;
}

.answer-history h2 {
  margin-bottom: 30px;
  text-align: center;
  font-size: 28px;
  font-weight: 800;
  background: var(--tech-gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.history-card {
  background: var(--tech-glass-bg-strong);
  backdrop-filter: blur(var(--tech-glass-blur));
  -webkit-backdrop-filter: blur(var(--tech-glass-blur));
  border: 1px solid var(--tech-glass-border);
  border-radius: var(--tech-radius-lg);
  box-shadow: var(--tech-card-shadow);
  padding: 40px;
  text-align: center;
}

.history-card p {
  color: var(--tech-text-secondary);
  font-size: 16px;
}
</style>
