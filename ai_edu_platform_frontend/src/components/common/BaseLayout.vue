<!-- 基础布局组件 - Cyberpunk 2.0 -->
<template>
  <div class="base-layout">
    <header class="layout-header">
      <slot name="header"></slot>
    </header>
    <main class="layout-main">
      <slot name="default"></slot>
    </main>
    <footer class="layout-footer">
      <slot name="footer"></slot>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.base-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg-gradient);
}

.layout-header {
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.layout-main {
  flex: 1;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  animation: terminal-fade-in 0.6s ease-out;
}

.layout-footer {
  flex-shrink: 0;
}
</style>
