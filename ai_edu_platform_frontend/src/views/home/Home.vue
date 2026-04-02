<template>
  <div class="home">
    <el-container>
      <el-main>
        <div class="hero-section">
          <h2>欢迎使用 AI 教育平台</h2>
          <p>通过AI技术实现"精准练"与"科学评"的结合，解决学习盲目性与信息不对称的核心痛点,为开发者提供从技术挑战到价值评估的一站式服务。</p>
        </div>
        <el-row :gutter="20" class="feature-section">
          <!-- 左侧个人信息列表 -->
          <el-col :span="6">
            <el-card shadow="hover" class="personal-info-card" v-if="isLoggedIn">
              <template #header>
                <div class="card-header">
                  <span>个人信息</span>
                </div>
              </template>
              <div class="personal-info-content">
                <el-avatar :size="80" class="avatar">
                  {{ userInfo?.username?.charAt(0) || 'U' }}
                </el-avatar>
                <h3>{{ userInfo?.username || '未登录' }}</h3>
                <div class="info-item">
                  <span class="label">身份：</span>
                  <span>{{ userInfo?.identity || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">期望薪资：</span>
                  <span>{{ userInfo?.salary ? userInfo.salary + '元' : '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">答题次数：</span>
                  <span>{{ userInfo?.answer_times || 0 }}</span>
                </div>
                <div class="info-item">
                  <span class="label">平均得分：</span>
                  <span>{{ userInfo?.average_score || 0 }}</span>
                </div>
                <el-button type="primary" @click="navigateTo('/personal/info')" class="edit-btn">
                  编辑信息
                </el-button>
              </div>
            </el-card>
            <el-card shadow="hover" class="personal-info-card" v-else>
              <template #header>
                <div class="card-header">
                  <span>个人信息</span>
                </div>
              </template>
              <div class="personal-info-content">
                <el-avatar :size="80" class="avatar">U</el-avatar>
                <h3>欢迎使用AI教育平台</h3>
                <p>登录后查看个人信息和学习记录</p>
                <el-button type="primary" @click="navigateTo('/login')" class="login-btn">
                  登录/注册
                </el-button>
              </div>
            </el-card>
          </el-col>
          <!-- 右侧功能模块选择按钮 -->
          <el-col :span="18">
            <el-row :gutter="20" class="feature-cards">
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header">
                      <span class="card-icon">📝</span>
                      <span>题目练习</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>提供各种类型的题目练习，帮助您巩固知识。</p>
                    <el-button type="primary" @click="navigateTo('/question/input')" class="feature-btn">开始练习</el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header">
                      <span class="card-icon">💰</span>
                      <span>薪资评估</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>查询不同行业、不同职位的薪资水平。</p>
                    <el-button type="primary" @click="navigateTo('/salary/input')" class="feature-btn">开始查询</el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header">
                      <span class="card-icon">👤</span>
                      <span>个人中心</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>管理个人信息，查看历史记录。</p>
                    <el-button type="primary" @click="navigateTo('/personal/info')" class="feature-btn">进入中心</el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-main>
      <el-footer>
        <div class="footer-content">
          <p>© 2026 AI 教育平台 版权所有</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 页面加载时获取用户信息
onMounted(async () => {
  isLoggedIn.value = userStore.isLoggedIn
  if (isLoggedIn.value) {
    await userStore.getUserInfo()
    userInfo.value = userStore.userInfo
  }
})

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-container {
  width: 100%;
  min-height: 100vh;
}
/* 英雄区域 */
.hero-section {
  text-align: center;
  padding: 40px 0;
  background: linear-gradient(135deg, #409EFF 0%, #667eea 100%);
  color: white;
  margin-bottom: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.hero-section h2 {
  margin-bottom: 20px;
  font-size: 32px;
  font-weight: bold;
}

.hero-section p {
  font-size: 18px;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
}

/* 功能区域 */
.feature-section {
  margin-top: 40px;
}

.feature-cards {
  margin-top: 20px;
}

/* 功能卡片 */
.feature-card {
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.card-icon {
  font-size: 24px;
  margin-right: 10px;
}

.card-header span:last-child {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.card-content {
  padding: 30px 20px;
  text-align: center;
}

.card-content p {
  margin-bottom: 30px;
  line-height: 1.6;
  color: #606266;
}

/* 按钮样式 */
.feature-btn {
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.feature-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 个人信息卡片 */
.personal-info-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.personal-info-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.personal-info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
}

.avatar {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.personal-info-content:hover .avatar {
  transform: scale(1.1);
}

.personal-info-content h3 {
  margin-bottom: 25px;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.info-item {
  width: 100%;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  padding: 8px 10px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.info-item:hover {
  background-color: #f8f9fa;
  border-radius: 4px;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
}

.info-item span:last-child {
  color: #303133;
  font-weight: 500;
}

.edit-btn,
.login-btn {
  margin-top: 25px;
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.edit-btn:hover,
.login-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 页脚 */
.el-footer {
  background-color: #303133;
  color: white;
  line-height: 60px;
  text-align: center;
  margin-top: 60px;
}

.footer-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 20px;
  }

  .hero-section {
    padding: 30px 20px;
  }

  .hero-section h2 {
    font-size: 24px;
  }

  .hero-section p {
    font-size: 16px;
  }

  .feature-section {
    margin-top: 30px;
  }

  .el-col {
    margin-bottom: 20px;
  }

  .personal-info-card,
  .feature-card {
    margin-bottom: 20px;
  }
}
</style>