<!-- Vue3 根组件 -->
<template>
  <div id="app">
    <router-view />
    <GlobalFooter />
  </div>
</template>

<script setup>
import GlobalFooter from './components/common/GlobalFooter.vue'
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
