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
}
</style>
