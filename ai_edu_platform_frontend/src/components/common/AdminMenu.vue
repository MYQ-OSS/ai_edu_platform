<template>
  <el-menu
    :default-active="activeMenu"
    class="admin-menu"
    mode="horizontal"
    router
  >
    <!-- 普通用户和管理员都可见的菜单 -->
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

    <!-- 仅管理员可见的菜单 -->
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

    <!-- 个人中心 -->
    <el-menu-item v-if="isLoggedIn" index="/personal/info">
      <el-icon><Avatar /></el-icon>
      <span>个人中心</span>
    </el-menu-item>

    <!-- 登录/注册 -->
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
  House,
  Edit,
  Money,
  Setting,
  User,
  Document,
  Collection,
  Avatar,
  Key
} from '@element-plus/icons-vue'
const route = useRoute()
const userStore = useUserStore()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 是否登录
const isLoggedIn = computed(() => userStore.getIsLoggedIn)

// 是否为管理员
const isAdmin = computed(() => userStore.isAdmin)
</script>

<style scoped>
.admin-menu {
  border-bottom: none;
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--panel-border);
  position: relative;
}

/* 顶部装饰线 */
.admin-menu::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-green), var(--neon-cyan), transparent);
  opacity: 0.6;
}

.admin-menu :deep(.el-menu--horizontal) {
  background: transparent;
  border-bottom: 1px solid var(--panel-border);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
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
  color: var(--neon-green);
  background: rgba(0, 255, 65, 0.05);
  border-bottom-color: var(--neon-green);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item.is-active .el-icon) {
  color: var(--neon-green);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: rgba(0, 212, 255, 0.06);
  color: var(--text-primary);
  border-bottom-color: var(--neon-cyan);
}

.admin-menu :deep(.el-menu--horizontal > .el-menu-item:hover .el-icon) {
  color: var(--neon-cyan);
}

.admin-menu :deep(.el-sub-menu__title) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
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
  color: var(--neon-green);
  border-bottom-color: var(--neon-green);
}

.admin-menu :deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title .el-icon) {
  color: var(--neon-green);
}

/* 下拉菜单终端风格 */
.admin-menu :deep(.el-menu--horizontal .el-menu--popup) {
  background: var(--bg-secondary);
  border: 1px solid var(--panel-border);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5), 0 0 10px rgba(0, 212, 255, 0.1);
}

.admin-menu :deep(.el-menu--horizontal .el-menu--popup .el-menu-item) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
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
  color: var(--neon-green);
  background: rgba(0, 255, 65, 0.1);
  border-left-color: var(--neon-green);
}
</style>
