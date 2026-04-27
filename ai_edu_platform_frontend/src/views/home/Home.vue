<template>
  <div class="home">
    <el-container>
      <el-main>
        <div class="hero-section">
          <div class="hero-header">
            <span class="hero-tag">SYSTEM</span>
            <span class="hero-tag status">ONLINE</span>
          </div>
          <h2>> AI 教育平台_</h2>
          <p>
            通过AI技术实现"精准练"与"科学评"的结合，解决学习盲目性与信息不对称的核心痛点,为开发者提供从技术挑战到价值评估的一站式服务。</p>
        </div>
        <el-row :gutter="20" class="feature-section" type="flex" align="stretch">
          <!-- 左侧个人信息 -->
          <el-col :span="6">
            <el-card shadow="hover" class="personal-info-card" v-if="isLoggedIn">
              <template #header>
                <div class="card-header center-header">
                  <span class="header-icon">></span>
                  <span>个人信息</span>
                  <span class="status-dot"></span>
                </div>
              </template>
              <div class="personal-info-content">
                <el-avatar :size="80" class="avatar" src="/程序员.png">
                  {{ userInfo?.username?.charAt(0) || 'U' }}
                </el-avatar>
                <h3>{{ userInfo?.username || '未登录' }}</h3>
                <div class="info-item">
                  <span class="label">身份</span>
                  <span class="value">{{ userInfo?.identity || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">期望薪资</span>
                  <span class="value salary">{{ userInfo?.salary ? userInfo.salary + '元' : '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">答题次数</span>
                  <span class="value">{{ userInfo?.answerTimes || 0 }}</span>
                </div>
                <div class="info-item">
                  <span class="label">平均得分</span>
                  <span class="value score">{{ userInfo?.averageScore || 0 }}</span>
                </div>
                <div class="button-group">
                  <el-button type="primary" @click="navigateTo('/personal/edit-info')" class="edit-btn">
                    编辑信息
                  </el-button>
                  <el-button @click="handleLogout" class="logout-btn">
                    退出登录
                  </el-button>
                </div>
              </div>
            </el-card>
            <el-card shadow="hover" class="personal-info-card" v-else>
              <template #header>
                <div class="card-header center-header">
                  <span class="header-icon">></span>
                  <span>个人信息</span>
                </div>
              </template>
              <div class="personal-info-content">
                <el-avatar :size="80" class="avatar" src="/程序员.png">U</el-avatar>
                <h3>欢迎使用AI教育平台</h3>
                <p>登录后查看个人信息和学习记录</p>
                <el-button type="primary" @click="navigateTo('/login')" class="login-btn">
                  登录/注册
                </el-button>
              </div>
            </el-card>
          </el-col>
          <!-- 右侧功能模块 -->
          <el-col :span="18">
            <el-row :gutter="20" class="feature-cards">
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">&lt;/&gt;</span>
                      <span>题目练习</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>提供各种类型的题目练习，帮助您巩固知识。</p>
                    <el-button type="primary" @click="navigateTo('/question/input')" class="feature-btn">开始练习
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">$</span>
                      <span>薪资评估</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>查询不同行业、不同职位的薪资水平。</p>
                    <el-button type="primary" @click="navigateTo('/salary/input')" class="feature-btn">开始查询
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">#</span>
                      <span>个人中心</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>管理个人信息，查看历史记录。</p>
                    <el-button type="primary" @click="navigateTo('/personal/info')" class="feature-btn">进入中心
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 管理员功能 -->
            <el-row :gutter="20" class="feature-cards admin-section" v-if="isAdmin">
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card admin-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">@</span>
                      <span>用户管理</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>管理系统用户账号和权限。</p>
                    <el-button type="warning" @click="navigateTo('/admin/user-manage')" class="feature-btn">用户管理
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card admin-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">~</span>
                      <span>题库管理</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>管理题目库，新增、编辑和删除题目。</p>
                    <el-button type="warning" @click="navigateTo('/admin/question-manage')" class="feature-btn">题库管理
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card admin-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">!</span>
                      <span>字典管理</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>管理系统基础配置数据。</p>
                    <el-button type="warning" @click="navigateTo('/admin/data-dict')" class="feature-btn">字典管理
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 普通用户推荐 -->
            <el-row :gutter="20" class="feature-cards recommend-section" v-else>
              <el-col :span="8">
                <el-card shadow="hover" class="feature-card recommend-card">
                  <template #header>
                    <div class="card-header center-header">
                      <span class="card-icon">^</span>
                      <span>薪资报告</span>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>查看历史薪资评估报告和建议。</p>
                    <el-button type="primary" @click="navigateTo('/personal/info?tab=salary-report')" class="feature-btn">查看报告
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
              <el-card shadow="hover" class="feature-card recommend-card">
                <template #header>
                  <div class="card-header center-header">
                    <span class="card-icon">*</span>
                    <span>我的收藏</span>
                  </div>
                </template>
                <div class="card-content">
                  <p>回顾收藏的题目，巩固知识点。</p>
                  <el-button type="primary" @click="navigateTo('/personal/info?tab=my-collect')" class="feature-btn">查看收藏
                  </el-button>
                </div>
              </el-card>
            </el-col>
              <el-col :span="8">
              <el-card shadow="hover" class="feature-card recommend-card">
                <template #header>
                  <div class="card-header center-header">
                    <span class="card-icon">%</span>
                    <span>学习统计</span>
                  </div>
                </template>
                <div class="card-content">
                  <p>查看你的答题记录和正确率趋势。</p>
                  <el-button type="primary" @click="navigateTo('/personal/learning-statistics')" class="feature-btn">查看详情
                  </el-button>
                </div>
              </el-card>
            </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {onMounted, ref, computed} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 是否为管理员
const isAdmin = computed(() => userStore.isAdmin)

// 页面加载时获取用户信息
onMounted(async () => {
  isLoggedIn.value = userStore.isLoggedIn
  if (isLoggedIn.value) {
    await userStore.fetchUserInfo()
    userInfo.value = userStore.userInfo
    // 获取答题统计信息
    await userStore.getQuizStatistics()
    userInfo.value = userStore.userInfo
  }
})

const navigateTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  userStore.logout()
  isLoggedIn.value = false
  userInfo.value = null
  router.push('/login')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  animation: terminal-fade-in 0.6s ease-out;
}

.el-container {
  width: 100%;
  min-height: 100vh;
}

/* 英雄区域 */
.hero-section {
  padding: 40px;
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
}

/* 顶部状态条 */
.hero-header {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.hero-tag {
  font-size: 11px;
  padding: 2px 8px;
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-xs);
  color: var(--text-muted);
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 1px;
}

.hero-tag.status {
  color: var(--neon-green);
  border-color: rgba(0, 255, 65, 0.3);
  animation: neon-pulse 2s ease-in-out infinite;
}

.hero-section h2 {
  margin-bottom: 16px;
  font-size: 32px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 1px;
}

.hero-section p {
  font-size: 15px;
  line-height: 1.8;
  max-width: 800px;
  color: var(--text-secondary);
}

/* 功能区域 */
.feature-section {
  margin-top: 20px;
}

.feature-cards {
  margin-top: 15px;
}

/* 功能卡片 */
.feature-card {
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
  border-radius: var(--radius-md);
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--panel-bg);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid var(--panel-border);
  box-shadow: var(--panel-glow);
}

.feature-card :deep(.el-card__body) {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.feature-card:hover {
  transform: translateY(-4px);
  border-color: var(--panel-border-active);
  box-shadow: var(--panel-glow-active);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px 16px;
  background: rgba(0, 255, 65, 0.03);
  border-bottom: 1px solid var(--divider);
  min-height: 50px;
  gap: 8px;
}

.card-header.center-header {
  position: relative;
}

.card-icon {
  font-size: 16px;
  color: var(--neon-cyan);
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
  transition: color var(--transition-base);
}

.feature-card:hover .card-icon {
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
}

.status-dot {
  position: absolute;
  right: 16px;
  width: 6px;
  height: 6px;
  background: var(--neon-green);
  border-radius: 50%;
  box-shadow: var(--glow-text-green);
  animation: neon-pulse 2s ease-in-out infinite;
}

.card-header span:not(.card-icon):not(.status-dot):not(.header-icon) {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.header-icon {
  color: var(--neon-green);
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
}

.card-content {
  padding: 24px 16px;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-content p {
  margin-bottom: 24px;
  line-height: 1.6;
  color: var(--text-secondary);
  font-size: 14px;
  min-height: 44px;
}

/* 按钮 */
.feature-btn {
  width: 100%;
  padding: 10px 0;
  font-size: 14px;
  font-weight: 700;
  transition: all var(--transition-base);
  border-radius: var(--radius-sm);
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 1px;
}

.feature-btn:hover {
  transform: translateY(-2px);
}

/* 个人信息卡片 */
.personal-info-card {
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--panel-bg);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid var(--panel-border);
  box-shadow: var(--panel-glow);
}

.personal-info-card :deep(.el-card__body) {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.personal-info-card :deep(.el-card__header) {
  background: rgba(0, 255, 65, 0.03);
  border-bottom: 1px solid var(--divider);
}

.personal-info-card:hover {
  border-color: var(--panel-border-active);
  box-shadow: var(--panel-glow-active);
}

.personal-info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  flex: 1;
}

.button-group {
  width: 100%;
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.avatar {
  margin-bottom: 16px;
  transition: transform var(--transition-base);
  border: 2px solid var(--panel-border);
}

.personal-info-content:hover .avatar {
  transform: scale(1.05);
}

.personal-info-content h3 {
  margin-bottom: 20px;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
}

.info-item {
  width: 100%;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  border-bottom: 1px solid var(--divider);
  border-radius: var(--radius-xs);
  font-size: 13px;
}

.info-item:hover {
  background: rgba(0, 255, 65, 0.03);
}

.info-item .label {
  font-weight: 600;
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.info-item .value {
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.info-item .value.salary {
  color: var(--neon-green);
}

.info-item .value.score {
  color: var(--neon-cyan);
  font-weight: 700;
}

.logout-btn {
  width: 80%;
  height: 36px;
  font-size: 13px;
  font-weight: 600;
  transition: all var(--transition-base);
  border-radius: var(--radius-sm);
  background: transparent;
  border: 1px solid var(--panel-border);
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.logout-btn:hover {
  background: rgba(255, 0, 128, 0.1);
  border-color: var(--neon-pink);
  color: var(--neon-pink);
}

.edit-btn,
.login-btn {
  width: 100%;
  height: 40px;
  font-size: 14px;
  font-weight: 700;
  transition: all var(--transition-base);
  border-radius: var(--radius-sm);
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 1px;
}

.edit-btn:hover,
.login-btn:hover {
  transform: translateY(-2px);
}

/* 管理员卡片 */
.admin-section {
  margin-top: 15px;
}

.admin-card {
  border-color: rgba(255, 107, 0, 0.2);
}

.admin-card:hover {
  border-color: var(--neon-orange);
  box-shadow: 0 0 16px rgba(255, 107, 0, 0.1);
}

.admin-card .card-icon {
  color: var(--neon-orange);
}

.admin-card .feature-btn:hover {
  box-shadow: 0 0 16px rgba(255, 107, 0, 0.2);
}

/* 推荐卡片 */
.recommend-section {
  margin-top: 15px;
}

.recommend-card {
  border-color: rgba(0, 212, 255, 0.2);
}

.recommend-card:hover {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 16px rgba(0, 212, 255, 0.1);
}

.recommend-card .card-icon {
  color: var(--neon-cyan);
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-section {
    padding: 24px;
  }

  .hero-section h2 {
    font-size: 24px;
  }

  .hero-section p {
    font-size: 14px;
  }

  .feature-section {
    margin-top: 20px;
  }

  .el-col {
    margin-bottom: 15px;
  }

  .personal-info-card,
  .feature-card {
    margin-bottom: 15px;
  }
}
</style>
