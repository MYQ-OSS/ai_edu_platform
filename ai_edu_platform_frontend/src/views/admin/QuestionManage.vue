<!-- 题库管理页面 -->
<template>
  <div class="question-manage-container">
    <!-- 页面标题 -->
    <h2>题库管理</h2>
    
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="题目名称">
          <el-input
            v-model="searchForm.questionName"
            placeholder="请输入题目名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="技术方向">
          <el-select
            v-model="searchForm.direction"
            placeholder="请选择技术方向"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in techDirections"
              :key="item.dictCode"
              :label="item.dictName"
              :value="item.dictName"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="目标薪资">
          <el-input-number
            v-model="searchForm.targetSalary"
            placeholder="目标薪资"
            :min="0"
            :step="1000"
            controls-position="right"
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增题目
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 题目列表表格 -->
    <el-card class="table-card">
      <el-table
        :data="questionList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="questionName" label="题目名称" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="direction" label="技术方向" width="150" align="center" />
        
        <el-table-column prop="targetSalary" label="目标薪资" width="120" align="center">
          <template #default="{ row }">
            <span class="salary-badge">¥{{ row.targetSalary }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="questionDesc" label="题目描述" min-width="250" show-overflow-tooltip />
        
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空状态 -->
      <div v-if="!loading && questionList.length === 0" class="empty-state">
        <el-empty description="暂无题目数据" />
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 返回按钮 -->
    <div class="back-button-section">
      <el-button @click="goBack" size="large">
        <el-icon><Back /></el-icon>
        返回首页
      </el-button>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="题目名称" prop="questionName">
          <el-input
            v-model="formData.questionName"
            placeholder="请输入题目名称（1-200字）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="技术方向" prop="direction">
          <el-select
            v-model="formData.direction"
            placeholder="请选择技术方向"
            style="width: 100%"
          >
            <el-option
              v-for="item in techDirections"
              :key="item.dictCode"
              :label="item.dictName"
              :value="item.dictName"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="目标薪资" prop="targetSalary">
          <el-input-number
            v-model="formData.targetSalary"
            placeholder="请输入目标薪资"
            :min="0"
            :step="1000"
            controls-position="right"
            style="width: 100%"
          />
          <div class="form-tip">单位：元</div>
        </el-form-item>
        
        <el-form-item label="题目描述" prop="questionDesc">
          <el-input
            v-model="formData.questionDesc"
            type="textarea"
            :rows="4"
            placeholder="请输入题目需求描述"
          />
        </el-form-item>
        
        <el-form-item label="选项配置" prop="options">
          <el-input
            v-model="formData.options"
            type="textarea"
            :rows="6"
            placeholder='请输入JSON格式，例如：[{"label":"SpringBoot","value":"springboot"},{"label":"MyBatis","value":"mybatis"}]'
          />
          <div class="form-tip">JSON数组格式，包含label和value字段</div>
          <el-button 
            size="small" 
            type="primary" 
            plain
            @click="formatOptions" 
            :disabled="!formData.options"
            title="格式化JSON选项"
            class="format-json-btn"
          >
            <el-icon><MagicStick /></el-icon>
            格式化JSON
          </el-button>
        </el-form-item>
        
        <el-form-item label="题目解析" prop="analysis">
          <el-input
            v-model="formData.analysis"
            type="textarea"
            :rows="4"
            placeholder="请输入题目解析（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Back, MagicStick } from '@element-plus/icons-vue'
import { getTechDirections } from '../../api/questionApi'
import { getQuestionList, addQuestion, updateQuestion, deleteQuestion } from '../../api/adminApi'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  questionName: '',
  direction: '',
  targetSalary: null
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 题目列表数据
const questionList = ref([])
const loading = ref(false)

// 技术方向字典
const techDirections = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const formData = reactive({
  id: null,
  questionName: '',
  questionDesc: '',
  options: '',
  targetSalary: null,
  direction: '',
  analysis: ''
})

// 表单验证规则
const formRules = {
  questionName: [
    { required: true, message: '请输入题目名称', trigger: 'blur' },
    { min: 1, max: 200, message: '题目名称长度在1-200个字符', trigger: 'blur' }
  ],
  direction: [
    { required: true, message: '请选择技术方向', trigger: 'change' }
  ],
  targetSalary: [
    { required: true, message: '请输入目标薪资', trigger: 'blur' }
  ],
  questionDesc: [
    { required: true, message: '请输入题目描述', trigger: 'blur' }
  ],
  options: [
    { required: true, message: '请输入选项配置', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            callback(new Error('选项必须是JSON数组格式'))
          } else {
            callback()
          }
        } catch (e) {
          callback(new Error('选项必须是有效的JSON格式'))
        }
      },
      trigger: 'blur'
    }
  ]
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 加载技术方向字典
 */
const loadTechDirections = async () => {
  try {
    const res = await getTechDirections()
    if (res.code === 200) {
      techDirections.value = res.data || []
    }
  } catch (error) {
    console.error('获取技术方向失败:', error)
  }
}

/**
 * 加载题目列表
 */
const loadQuestionList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      orderByColumn: 'id',
      isAsc: 'asc',
      ...searchForm
    }
    const res = await getQuestionList(params)
    if (res.code === 200) {
      questionList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载题目列表失败')
    }
  } catch (error) {
    console.error('加载题目列表失败:', error)
    ElMessage.error('加载题目列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.pageNum = 1
  loadQuestionList()
}

/**
 * 重置
 */
const handleReset = () => {
  searchForm.questionName = ''
  searchForm.direction = ''
  searchForm.targetSalary = null
  handleSearch()
}

/**
 * 新增题目
 */
const handleAdd = () => {
  dialogTitle.value = '新增题目'
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑题目
 */
const handleEdit = (row) => {
  dialogTitle.value = '编辑题目'
  Object.assign(formData, {
    id: row.id,
    questionName: row.questionName,
    questionDesc: row.questionDesc,
    options: row.options,
    targetSalary: row.targetSalary,
    direction: row.direction,
    analysis: row.analysis || ''
  })
  dialogVisible.value = true
}

/**
 * 删除题目
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除题目 "${row.questionName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await deleteQuestion(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadQuestionList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除题目失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 格式化JSON选项
 */
const formatOptions = () => {
  if (!formData.options) {
    ElMessage.warning('请先输入JSON内容')
    return
  }
  
  try {
    const parsed = JSON.parse(formData.options)
    formData.options = JSON.stringify(parsed, null, 2)
    ElMessage.success('格式化成功')
  } catch (e) {
    ElMessage.error('JSON格式错误，请检查输入')
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = { ...formData }
        let res
        
        if (formData.id) {
          // 编辑模式
          res = await updateQuestion(data)
        } else {
          // 新增模式
          res = await addQuestion(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(formData.id ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          loadQuestionList()
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (error) {
        console.error('保存题目失败:', error)
        ElMessage.error('保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

/**
 * 重置表单
 */
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    questionName: '',
    questionDesc: '',
    options: '',
    targetSalary: null,
    direction: '',
    analysis: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

/**
 * 每页条数变化
 */
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadQuestionList()
}

/**
 * 页码变化
 */
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadQuestionList()
}

/**
 * 返回首页
 */
const goBack = () => {
  router.push('/home')
}

// 页面加载时初始化
onMounted(() => {
  loadTechDirections()
  loadQuestionList()
})
</script>

<style scoped>
.question-manage-container {
  padding: 30px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

.question-manage-container h2 {
  margin-bottom: 25px;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  min-height: 500px;
}

.salary-badge {
  color: #67c23a;
  font-weight: 600;
}

.empty-state {
  padding: 40px 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.5;
}

.format-json-btn {
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .question-manage-container {
    padding: 20px 30px;
  }
}

@media (max-width: 768px) {
  .question-manage-container {
    padding: 15px 20px;
  }

  .question-manage-container h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }

  .search-card {
    margin-bottom: 15px;
  }

  .search-form {
    flex-direction: column;
    gap: 10px;
  }

  .search-form :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .search-form :deep(.el-input),
  .search-form :deep(.el-select),
  .search-form :deep(.el-input-number) {
    width: 100% !important;
  }

  .search-form :deep(.el-form-item__content) {
    width: 100%;
  }

  .table-card {
    min-height: auto;
  }

  /* 表格在小屏幕上横向滚动 */
  .table-card :deep(.el-table) {
    font-size: 14px;
  }

  .pagination-container {
    justify-content: center;
  }

  .pagination-container :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .question-manage-container {
    padding: 10px 15px;
  }

  .question-manage-container h2 {
    font-size: 20px;
    margin-bottom: 15px;
  }

  .search-form :deep(.el-button) {
    width: 100%;
  }

  .back-button-section :deep(.el-button) {
    width: 100%;
  }
}
</style>
