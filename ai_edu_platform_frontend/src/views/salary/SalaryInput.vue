<!-- 薪资评估页 -->
<template>
  <div class="salary-input-container">
    <h2>薪资评估</h2>
    <div class="input-card">
      <!-- 步骤指示器 -->
      <el-steps :active="activeStep" finish-status="success" class="steps">
        <el-step title="基本信息" />
        <el-step title="技术能力" />
        <el-step title="项目经验" />
      </el-steps>
      
      <!-- 表单内容 -->
      <div class="form-content">
        <!-- 基本信息步骤 -->
        <div v-if="activeStep === 0" class="step-content">
          <el-form :model="formData.basic" :rules="rules.basic" ref="basicFormRef" label-width="140px">
            <el-form-item label="学历" prop="education">
              <el-select v-model="formData.basic.education" placeholder="请选择学历" size="large">
                <el-option label="高中及以下" value="highSchool" />
                <el-option label="大专" value="college" />
                <el-option label="本科" value="bachelor" />
                <el-option label="硕士" value="master" />
                <el-option label="博士" value="phd" />
              </el-select>
            </el-form-item>
            <el-form-item label="工作经验年限" prop="experience">
              <el-select v-model="formData.basic.experience" placeholder="请选择工作经验" size="large">
                <el-option label="应届毕业生" value="fresh" />
                <el-option label="1-3年" value="1-3" />
                <el-option label="3-5年" value="3-5" />
                <el-option label="5-10年" value="5-10" />
                <el-option label="10年以上" value="10+" />
              </el-select>
            </el-form-item>
            <el-form-item label="目标城市" prop="city">
              <el-input v-model="formData.basic.city" placeholder="请输入期望工作的城市" size="large" />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 技术能力步骤 -->
        <div v-if="activeStep === 1" class="step-content">
          <el-form :model="formData.tech" :rules="rules.tech" ref="techFormRef" label-width="140px">
            <el-form-item label="技术方向" prop="direction">
              <el-select v-model="formData.tech.direction" placeholder="请选择技术方向" size="large">
                <el-option 
                  v-for="item in techDirections" 
                  :key="item.dictCode" 
                  :label="item.dictName" 
                  :value="item.dictName"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="核心技术栈" prop="skills">
              <div class="skill-container">
                <el-tag
                  v-for="skill in formData.tech.skills"
                  :key="skill"
                  closable
                  @close="removeSkill(skill)"
                  class="skill-tag"
                  size="large"
                >
                  {{ skill }}
                </el-tag>
                <el-input
                  v-model="newSkill"
                  placeholder="输入技术名称，按 Enter 添加"
                  @keyup.enter="addSkill"
                  class="skill-input"
                  size="large"
                />
              </div>
            </el-form-item>
            <el-form-item label="技术熟练度" prop="proficiency">
              <el-slider
                v-model="formData.tech.proficiency"
                :min="1"
                :max="5"
                :marks="{
                  1: '入门',
                  2: '基础',
                  3: '熟练',
                  4: '精通',
                  5: '专家'
                }"
                :step="1"
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 项目经验步骤 -->
        <div v-if="activeStep === 2" class="step-content">
          <el-form :model="formData.project" :rules="rules.project" ref="projectFormRef" label-width="140px">
            <el-form-item label="项目数量" prop="count">
              <el-input v-model.number="formData.project.count" type="number" placeholder="请输入参与的项目数量" size="large" />
            </el-form-item>
            <el-form-item label="项目经历描述" prop="description">
              <el-input
                v-model="formData.project.description"
                type="textarea"
                placeholder="请详细描述您参与的项目，包括：&#10;1. 项目规模和技术栈&#10;2. 您的职责和贡献&#10;3. 项目成果和亮点"
                :rows="6"
              />
            </el-form-item>
            <el-form-item label="大型项目经验" prop="hasLargeProject">
              <el-switch v-model="formData.project.hasLargeProject" size="large" />
              <span class="form-tip">是否参与过用户量大、并发高的项目</span>
            </el-form-item>
            <el-form-item label="团队管理经验" prop="hasManagement">
              <el-switch v-model="formData.project.hasManagement" size="large" />
              <span class="form-tip">是否有带领团队或指导新人的经验</span>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <!-- 按钮区域 -->
      <div class="button-section">
        <el-button v-if="activeStep > 0" @click="prevStep">上一步</el-button>
        <el-button v-if="activeStep < 2" type="primary" @click="nextStep" :loading="loading">下一步</el-button>
        <el-button v-if="activeStep === 2" type="primary" @click="handleSubmit" :loading="loading">开始评估</el-button>
        <el-button @click="goBack">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { evaluateSalary, getTechDirections } from '../../api/salaryApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()
const activeStep = ref(0)
const loading = ref(false)
const basicFormRef = ref(null)
const techFormRef = ref(null)
const projectFormRef = ref(null)
const newSkill = ref('')

// 技术方向列表
const techDirections = ref([])

// 表单数据
const formData = reactive({
  basic: {
    education: '',
    experience: '',
    city: ''
  },
  tech: {
    direction: '',
    skills: [],
    proficiency: 3
  },
  project: {
    count: '',
    description: '',
    hasLargeProject: false,
    hasManagement: false
  }
})

// 验证规则
const rules = {
  basic: {
    education: [
      { required: true, message: '请选择学历', trigger: 'change' }
    ],
    experience: [
      { required: true, message: '请选择工作经验', trigger: 'change' }
    ],
    city: [
      { required: true, message: '请输入目标城市', trigger: 'blur' }
    ]
  },
  tech: {
    direction: [
      { required: true, message: '请选择技术方向', trigger: 'change' }
    ],
    skills: [
      { required: true, message: '请至少添加一项技术', trigger: 'blur' }
    ]
  },
  project: {
    count: [
      { required: true, message: '请输入项目数量', trigger: 'blur' },
      { type: 'number', message: '请输入有效的项目数量', trigger: 'blur' }
    ],
    description: [
      { required: true, message: '请描述您的项目经历', trigger: 'blur' }
    ]
  }
}

// 页面加载时获取技术方向字典数据
onMounted(async () => {
  await loadTechDirections()
})

// 加载技术方向字典数据
const loadTechDirections = async () => {
  try {
    const response = await getTechDirections()
    if (response.code === 200) {
      techDirections.value = response.data || []
    }
  } catch (error) {
    console.error('获取技术方向失败:', error)
  }
}

// 上一步
const prevStep = () => {
  activeStep.value--
}

// 下一步
const nextStep = async () => {
  let valid = true
  
  if (activeStep.value === 0) {
    await basicFormRef.value.validate((v) => {
      valid = v
    })
  } else if (activeStep.value === 1) {
    await techFormRef.value.validate((v) => {
      valid = v
    })
  }
  
  if (valid) {
    activeStep.value++
  }
}

// 添加技术
const addSkill = () => {
  if (newSkill.value && !formData.tech.skills.includes(newSkill.value)) {
    formData.tech.skills.push(newSkill.value)
    newSkill.value = ''
  }
}

// 移除技术
const removeSkill = (skill) => {
  const index = formData.tech.skills.indexOf(skill)
  if (index !== -1) {
    formData.tech.skills.splice(index, 1)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  await projectFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构造请求参数
        const requestData = {
          userId: userStore.userInfo?.id,
          direction: formData.tech.direction,
          city: formData.basic.city || '',
          experience: formData.project.description,
          education: formData.basic.education || '',
          identity: formData.basic.experience === 'fresh' ? '学生' : '在职'
        }
        
        // 调用薪资评估接口
        const response = await evaluateSalary(requestData)
        
        if (response.code === 200) {
          ElMessage.success('薪资评估报告生成成功')
          // 保存reportId到localStorage
          localStorage.setItem('salaryReportId', response.data.reportId)
          router.push('/salary/report')
        } else {
          ElMessage.error(response.msg || '评估失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '评估失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 返回首页
const goBack = () => {
  router.push('/home')
}
</script>

<style scoped>
.salary-input-container {
  padding: 40px;
  max-width: 900px;
  margin: 0 auto;
  animation: terminal-fade-in 0.6s ease-out;
  position: relative;
}

/* 装饰光斑 */
.salary-input-container::before {
  content: '';
  position: fixed;
  top: 10%;
  left: 5%;
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(0, 255, 65, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  animation: corner-pulse 10s ease-in-out infinite;
  z-index: 0;
}

.salary-input-container h2 {
  margin-bottom: 30px;
  text-align: center;
  font-size: 28px;
  font-weight: 800;
  color: var(--neon-green);
  text-shadow: var(--glow-text-green);
  letter-spacing: 2px;
  font-family: 'JetBrains Mono', monospace;
  position: relative;
  z-index: 1;
}

.salary-input-container h2::before {
  content: '> ';
  color: var(--neon-cyan);
  animation: cursor-blink 1s step-end infinite;
}

.input-card {
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--panel-border);
  padding: 36px;
  border-radius: var(--radius-md);
  box-shadow: var(--panel-glow);
  position: relative;
  z-index: 1;
}

.steps {
  margin-bottom: 30px;
}

.steps :deep(.el-step__title) {
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
}

.steps :deep(.el-step__description) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
}

.steps :deep(.el-step__head.is-success) {
  color: var(--neon-green);
  border-color: var(--neon-green);
}

.steps :deep(.el-step__head.is-process) {
  color: var(--neon-cyan);
  border-color: var(--neon-cyan);
}

.steps :deep(.el-step__line) {
  background-color: var(--divider);
}

.step-content {
  margin-bottom: 24px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.el-form-item :deep(.el-input__wrapper),
.el-form-item :deep(.el-select .el-input__wrapper) {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--panel-border);
  box-shadow: none;
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.el-form-item :deep(.el-input__wrapper:hover),
.el-form-item :deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--neon-cyan);
}

.el-form-item :deep(.el-input__wrapper.is-focus),
.el-form-item :deep(.el-select .el-input__wrapper.is-focus) {
  border-color: var(--neon-green);
  box-shadow: var(--glow-green);
}

.el-form-item :deep(.el-input__inner) {
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.el-form-item :deep(.el-textarea__inner) {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--panel-border);
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.el-form-item :deep(.el-textarea__inner:focus) {
  border-color: var(--neon-green);
  box-shadow: var(--glow-green);
}

.el-form-item :deep(.el-select-dropdown) {
  background: var(--bg-tertiary);
  border: 1px solid var(--panel-border);
}

.el-form-item :deep(.el-select-dropdown__item) {
  color: var(--text-primary);
  font-family: 'JetBrains Mono', monospace;
}

.el-form-item :deep(.el-select-dropdown__item:hover) {
  background: var(--panel-bg);
}

.el-form-item :deep(.el-select-dropdown__item.is-selected) {
  color: var(--neon-green);
  font-weight: 700;
}

.form-tip {
  margin-left: 12px;
  color: var(--text-muted);
  font-size: 13px;
  font-family: 'JetBrains Mono', monospace;
}

.skill-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.skill-tag {
  margin-bottom: 8px;
}

.skill-tag :deep(.el-tag) {
  background: rgba(0, 255, 65, 0.1);
  border-color: rgba(0, 255, 65, 0.3);
  color: var(--neon-green);
  font-family: 'JetBrains Mono', monospace;
}

.skill-input {
  width: 100%;
  margin-top: 10px;
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.button-section .el-button {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 700;
  letter-spacing: 1px;
}
</style>