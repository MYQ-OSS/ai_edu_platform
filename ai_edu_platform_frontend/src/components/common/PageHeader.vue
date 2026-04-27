<!-- 页面头部组件 - Cyberpunk 2.0 -->
<template>
  <div class="page-header">
    <div class="header-glow"></div>
    <div class="header-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-header {
  position: relative;
  padding: 20px 0;
  margin-bottom: 24px;
  overflow: hidden;
}

.header-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-purple), var(--neon-pink), transparent);
  opacity: 0.5;
}

.header-content {
  position: relative;
  z-index: 1;
}
</style>
