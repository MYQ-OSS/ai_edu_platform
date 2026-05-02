<!-- 首页 - Cyberpunk 2.0 -->
<template>
  <div class="home">
    <ParticleBackground
      :zIndex="0"
      :particleCount="20"
      particleColor="mixed"
      :speed="0.15"
      :interactive="false"
    />
    <el-container>
      <el-main>
        <!-- 英雄区域 -->
        <div class="hero-section">
          <div class="hero-bg-image"></div>
          <div class="hero-content-wrapper">
            <div class="hero-tag-line">
              <span class="hero-tag">AI SYSTEM</span>
              <span class="hero-tag status">LIVE</span>
            </div>
            <h1 class="hero-title">AI EDU PLATFORM</h1>
            <p class="hero-subtitle">
              <TypewriterText text="通过AI技术实现精准练与科学评的结合，为开发者提供从技术挑战到价值评估的一站式服务" :speed="40" :delay="800" />
            </p>
            <div class="hero-stats" v-if="isLoggedIn">
              <div class="stat-item">
                <NumberCounter :target="userInfo?.answerTimes || 0" />
                <span class="stat-label">答题次数</span>
              </div>
              <div class="stat-item">
                <NumberCounter :target="userInfo?.averageScore || 0" :decimals="1" />
                <span class="stat-label">平均得分</span>
              </div>
              <div class="stat-item">
                <NumberCounter :target="userInfo?.salary || 0" suffix=" 元" />
                <span class="stat-label">期望薪资</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 主内容区 -->
        <div class="content-area">
          <!-- 用户信息卡片 -->
          <div class="user-profile-section" v-if="isLoggedIn">
            <div class="profile-card">
              <div class="profile-header">
                <el-avatar :size="80" class="avatar" src="/程序员.png">
                  {{ userInfo?.username?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <h3>{{ userInfo?.username || '未登录' }}</h3>
                  <div class="info-tags">
                    <el-tag type="info" size="small">{{ userInfo?.identity || '未设置' }}</el-tag>
                    <el-tag type="success" size="small">{{ userInfo?.answerTimes || 0 }}次答题</el-tag>
                    <el-tag type="warning" size="small">{{ userInfo?.averageScore || 0 }}分</el-tag>
                  </div>
                </div>
              </div>
              <div class="profile-stats">
                <div class="stat-box">
                  <span class="stat-value">{{ userInfo?.answerTimes || 0 }}</span>
                  <span class="stat-desc">答题次数</span>
                </div>
                <div class="stat-box">
                  <span class="stat-value">{{ userInfo?.averageScore || 0 }}</span>
                  <span class="stat-desc">平均得分</span>
                </div>
                <div class="stat-box">
                  <span class="stat-value">{{ userInfo?.salary || 0 }}</span>
                  <span class="stat-desc">期望薪资</span>
                </div>
              </div>
              <div class="profile-actions">
                <el-button type="primary" @click="navigateTo('/personal/edit-info')">编辑资料</el-button>
                <el-button type="danger" @click="handleLogout">退出登录</el-button>
              </div>
            </div>
          </div>

          <!-- 未登录提示 -->
          <div class="login-prompt-section" v-else>
            <div class="login-prompt-card">
              <div class="login-prompt-icon">👤</div>
              <h3>欢迎使用AI教育平台</h3>
              <p>登录后解锁更多功能和个性化体验</p>
              <el-button type="primary" @click="navigateTo('/login')" size="large">立即登录</el-button>
            </div>
          </div>

          <!-- 功能卡片区域 -->
          <div class="features-section">
            <h2 class="section-title">核心功能</h2>
            <div class="feature-grid">
              <div class="feature-item">
                <div class="feature-icon">
                  <span class="icon">💻</span>
                </div>
                <h3>题目练习</h3>
                <p>提供各种类型的题目练习，帮助您巩固知识</p>
                <el-button type="primary" @click="navigateTo('/question/input')">开始练习</el-button>
              </div>

              <div class="feature-item">
                <div class="feature-icon">
                  <span class="icon">$</span>
                </div>
                <h3>薪资评估</h3>
                <p>查询不同行业、不同职位的薪资水平</p>
                <el-button type="primary" @click="navigateTo('/salary/input')">薪资查询</el-button>
              </div>

              <div class="feature-item" v-if="isLoggedIn">
                <div class="feature-icon">
                  <span class="icon">🤖</span>
                </div>
                <h3>AI对话</h3>
                <p>与AI助手对话，分析答题记录和职业规划</p>
                <el-button type="primary" @click="navigateTo('/chat')">开始对话</el-button>
              </div>

              <div class="feature-item">
                <div class="feature-icon">
                  <span class="icon">📊</span>
                </div>
                <h3>个人中心</h3>
                <p>管理个人信息，查看历史记录</p>
                <el-button type="primary" @click="navigateTo('/personal/info')">个人中心</el-button>
              </div>

              <div class="feature-item">
                <div class="feature-icon">
                  <span class="icon">📈</span>
                </div>
                <h3>学习统计</h3>
                <p>查看你的答题记录和正确率趋势</p>
                <el-button type="primary" @click="navigateTo('/personal/learning-statistics')">学习统计</el-button>
              </div>
            </div>
          </div>

          <!-- 管理员功能区域 -->
          <div class="admin-section" v-if="isAdmin">
            <h2 class="section-title">管理功能</h2>
            <div class="admin-grid">
              <div class="admin-item">
                <div class="admin-icon">
                  <span class="icon">👤</span>
                </div>
                <h3>用户管理</h3>
                <p>管理系统用户账号和权限</p>
                <el-button type="warning" @click="navigateTo('/admin/user-manage')">用户管理</el-button>
              </div>

              <div class="admin-item">
                <div class="admin-icon">
                  <span class="icon">📚</span>
                </div>
                <h3>题库管理</h3>
                <p>管理题目库，新增、编辑和删除题目</p>
                <el-button type="warning" @click="navigateTo('/admin/question-manage')">题库管理</el-button>
              </div>

              <div class="admin-item">
                <div class="admin-icon">
                  <span class="icon">⚙️</span>
                </div>
                <h3>字典管理</h3>
                <p>管理系统基础配置数据</p>
                <el-button type="warning" @click="navigateTo('/admin/data-dict')">字典管理</el-button>
              </div>
            </div>
          </div>

          <!-- 推荐功能区域 -->
          <div class="recommend-section" v-else>
            <h2 class="section-title">推荐功能</h2>
            <div class="recommend-grid">
              <div class="recommend-item">
                <div class="recommend-icon">
                  <span class="icon">📋</span>
                </div>
                <h3>薪资报告</h3>
                <p>查看历史薪资评估报告和建议</p>
                <el-button type="primary" @click="navigateTo('/personal/info?tab=salary-report')">薪资报告</el-button>
              </div>

              <div class="recommend-item">
                <div class="recommend-icon">
                  <span class="icon">⭐</span>
                </div>
                <h3>我的收藏</h3>
                <p>回顾收藏的题目，巩固知识点</p>
                <el-button type="primary" @click="navigateTo('/personal/info?tab=my-collect')">我的收藏</el-button>
              </div>

              <div class="recommend-item">
                <div class="recommend-icon">
                  <span class="icon">📈</span>
                </div>
                <h3>学习统计</h3>
                <p>查看你的答题记录和正确率趋势</p>
                <el-button type="primary" @click="navigateTo('/personal/learning-statistics')">学习统计</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/userStore'
import ParticleBackground from '../../components/common/ParticleBackground.vue'
import TypewriterText from '../../components/common/TypewriterText.vue'
import NumberCounter from '../../components/common/NumberCounter.vue'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = ref(false)
const userInfo = ref(null)

const isAdmin = computed(() => userStore.isAdmin)

onMounted(async () => {
  isLoggedIn.value = userStore.isLoggedIn
  if (isLoggedIn.value) {
    await userStore.fetchUserInfo()
    userInfo.value = userStore.userInfo
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
  position: relative;
  overflow-x: hidden;
}

.el-container {
  width: 100%;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.el-main {
  padding: 20px;
}

/* 英雄区域 */
.hero-section {
  padding: 80px 50px;
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--panel-glow);
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.hero-bg-image {
  position: absolute;
  inset: 0;
  background: url('https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=1200&q=60') center/cover no-repeat;
  opacity: 0.08;
  filter: blur(2px);
  z-index: 0;
}

.hero-content-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.hero-tag-line {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
}

.hero-tag {
  font-size: 12px;
  padding: 4px 12px;
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-xs);
  color: var(--text-muted);
  letter-spacing: 1px;
  background: rgba(0, 212, 255, 0.05);
  text-transform: uppercase;
}

.hero-tag.status {
  color: var(--neon-cyan);
  border-color: rgba(0, 212, 255, 0.3);
}

.hero-title {
  margin-bottom: 20px;
  font-size: 3.5rem;
  font-weight: 900;
  letter-spacing: 3px;
  color: var(--text-primary);
  text-shadow: none;
}

.hero-subtitle {
  font-size: 16px;
  line-height: 1.8;
  max-width: 800px;
  color: var(--text-secondary);
  min-height: 32px;
  margin: 0 auto 30px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--divider);
}

.stat-item {
  text-align: center;
  min-width: 100px;
}

.stat-item span:first-child {
  display: block;
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--neon-cyan);
  text-shadow: none;
  line-height: 1;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* 内容区域 */
.content-area {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

/* 用户信息区域 */
.user-profile-section {
  margin-bottom: 30px;
}

.profile-card {
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow);
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  margin-bottom: 20px;
}

.avatar {
  border: 3px solid var(--neon-cyan);
  box-shadow: none;
  transition: none;
}

.user-info {
  flex: 1;
  text-align: left;
}

.user-info h3 {
  margin: 0 0 10px 0;
  color: var(--text-primary);
  font-size: 1.5rem;
}

.info-tags {
  display: flex;
  gap: 8px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin: 20px 0;
  padding: 20px 0;
  border-top: 1px solid var(--divider);
  border-bottom: 1px solid var(--divider);
}

.stat-box {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 1.8rem;
  font-weight: 800;
  color: var(--neon-cyan);
  text-shadow: none;
}

.stat-desc {
  display: block;
  font-size: 0.9rem;
  color: var(--text-muted);
  margin-top: 4px;
}

.profile-actions {
  display: flex;
  gap: 12px;
  width: 100%;
  margin-top: 20px;
}

.profile-actions .el-button {
  flex: 1;
  width: 100%;
}

/* 登录提示卡片 */
.login-prompt-section {
  margin-bottom: 30px;
}

.login-prompt-card {
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow);
  padding: 40px;
  text-align: center;
}

.login-prompt-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.login-prompt-card h3 {
  margin: 0 0 10px 0;
  color: var(--text-primary);
  font-size: 1.5rem;
}

.login-prompt-card p {
  color: var(--text-secondary);
  margin-bottom: 20px;
}

/* 功能区域 */
.features-section,
.admin-section,
.recommend-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 30px;
  text-align: center;
  color: var(--text-primary);
}

/* 功能卡片网格 */
.feature-grid {
  display: flex;
  flex-wrap: nowrap;
  gap: 30px;
  width: 100%;
}

.feature-item {
  flex: 1;
  min-width: 0;
}

.admin-grid,
.recommend-grid {
  display: flex;
  flex-wrap: nowrap;
  gap: 30px;
  width: 100%;
}

.admin-item,
.recommend-item {
  flex: 1;
  min-width: 0;
}

.feature-item,
.admin-item,
.recommend-item {
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--panel-glow);
  padding: 30px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}

.feature-icon,
.admin-icon,
.recommend-icon {
  margin-bottom: 20px;
}

.feature-icon .icon,
.admin-icon .icon,
.recommend-icon .icon {
  font-size: 3rem;
}

.feature-item h3,
.admin-item h3,
.recommend-item h3 {
  margin: 0 0 15px 0;
  color: var(--text-primary);
  font-size: 1.4rem;
}

.feature-item p,
.admin-item p,
.recommend-item p {
  color: var(--text-secondary);
  margin-bottom: 20px;
  flex: 1;
  display: flex;
  align-items: center;
}

.feature-item .el-button,
.admin-item .el-button,
.recommend-item .el-button {
  width: 100%;
  margin-top: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .hero-title {
    font-size: 2.8rem;
  }

  .hero-stats {
    gap: 40px;
  }
}

@media (max-width: 992px) {
  .hero-section {
    padding: 60px 30px;
  }

  .hero-title {
    font-size: 2.2rem;
  }

  .hero-stats {
    gap: 30px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .user-info {
    text-align: center;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 20px;
  }

  .hero-title {
    font-size: 1.8rem;
  }

  .hero-stats {
    flex-wrap: wrap;
    gap: 20px;
  }

  .stat-item {
    min-width: 80px;
  }

  .stat-item span:first-child {
    font-size: 2rem;
  }

  .profile-stats {
    flex-wrap: wrap;
    gap: 20px;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .feature-grid,
  .admin-grid,
  .recommend-grid {
    flex-wrap: wrap;
    gap: 20px;
  }

  .feature-item,
  .admin-item,
  .recommend-item {
    flex: 1 1 calc(50% - 10px);
    min-height: auto;
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 30px 15px;
  }

  .hero-title {
    font-size: 1.5rem;
  }

  .profile-card,
  .login-prompt-card {
    padding: 20px;
  }
}
</style>