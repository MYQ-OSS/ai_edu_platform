<!-- 导航菜单 - Cyberpunk 2.0 -->
<template>
  <el-menu
    :default-active="activeMenu"
    class="admin-menu"
    mode="horizontal"
    router
  >
    <el-menu-item index="/home">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </el-menu-item>

    <el-menu-item index="/question/input">
      <el-icon><Edit /></el-icon>
      <span>题目练习</span>
    </el-menu-item>

    <el-menu-item index="/salary/input">
      <el-icon><Money /></el-icon>
      <span>薪资评估</span>
    </el-menu-item>

    <el-sub-menu v-if="isAdmin" index="admin">
      <template #title>
        <el-icon><Setting /></el-icon>
        <span>后台管理</span>
      </template>
      <el-menu-item index="/admin/user-manage">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/question-manage">
        <el-icon><Document /></el-icon>
        <span>题库管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/data-dict">
        <el-icon><Collection /></el-icon>
        <span>字典管理</span>
      </el-menu-item>
    </el-sub-menu>

    <el-menu-item v-if="isLoggedIn" index="/personal/info">
      <el-icon><Avatar /></el-icon>
      <span>个人中心</span>
    </el-menu-item>

    <el-menu-item v-if="!isLoggedIn" index="/login">
      <el-icon><Key /></el-icon>
      <span>登录</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../store/userStore'
import {
  House, Edit, Money, Setting, User, Document, Collection, Avatar, Key
} from '@element-plus/icons-vue'
const route = useRoute()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)
const isLoggedIn = computed(() => userStore.getIsLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)
</script>

<style scoped>
.admin-menu {
  border-bottom: none;
  background: var(--panel-bg-strong);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--panel-border);
  position: relative;
}

.admin-menu::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-purple), var(--neon-pink), transparent);
  opacity: 0.7;
}

.admin-menu :deep(.el-menu--horizontal) {
  background: transparent;
  border-bottom: 1px solid var(--panel-border);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item) {
  color: var(--text-secondary);
  font-size: 13px;
  letter-spacing: 0.5px;
  transition: all var(--transition-base);
  border-bottom: 2px solid transparent;
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item .el-icon) {
  color: var(--neon-cyan);
  transition: all var(--transition-base);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: var(--neon-cyan);
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.06), rgba(180, 74, 255, 0.03));
  border-bottom-color: var(--neon-cyan);
  text-shadow: var(--glow-text-cyan);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item.is-active .el-icon) {
  color: var(--neon-cyan);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.06);
  color: var(--text-primary);
  border-bottom-color: var(--neon-purple);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item:hover .el-icon) {
  color: var(--neon-purple);
}

.admin-menu :deep(.el-sub-menu__title) {
  color: var(--text-secondary);
  font-size: 13px;
  letter-spacing: 0.5px;
  transition: all var(--transition-base);
  border-bottom: 2px solid transparent;
}

.admin-menu :deep(.el-sub-menu__title .el-icon) {
  color: var(--neon-purple);
}

.admin-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(180, 74, 255, 0.06);
  color: var(--text-primary);
  border-bottom-color: var(--neon-purple);
}

.admin-menu :deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title) {
  color: var(--neon-cyan);
  border-bottom-color: var(--neon-cyan);
}

.admin-menu :deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title .el-icon) {
  color: var(--neon-cyan);
}

.admin-menu :deep(.el-menu--horizontal .el-menu--popup) {
  background: var(--panel-bg-strong);
  border: 1px solid var(--panel-border);
  backdrop-filter: blur(16px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.6), 0 0 20px rgba(0, 212, 255, 0.1);
}

.admin-menu :deep(.el-menu--horizontal .el-menu--popup .el-menu-item) {
  color: var(--text-secondary);
  font-size: 12px;
  border-left: 2px solid transparent;
  transition: all var(--transition-base);
}

.admin-menu :deep(.el-menu--horizontal .el-menu--popup .el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--neon-cyan);
  border-left-color: var(--neon-cyan);
}

.admin-menu :deep(.el-menu--horizontal .el-menu--popup .el-menu-item.is-active) {
  color: var(--neon-cyan);
  background: rgba(0, 212, 255, 0.1);
  border-left-color: var(--neon-cyan);
}
</style>
