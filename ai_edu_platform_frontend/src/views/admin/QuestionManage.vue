<!-- 题库管理 - 优化版 -->
<template>
  <div class="question-manage-container">
    <h2 class="page-title">题库管理</h2>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="题目名称">
          <el-input v-model="searchForm.questionName" placeholder="请输入题目名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="技术方向">
          <el-select v-model="searchForm.direction" placeholder="请选择技术方向" clearable style="width: 180px">
            <el-option v-for="item in techDirections" :key="item.dictCode" :label="item.dictName" :value="item.dictName" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标薪资">
          <el-input-number v-model="searchForm.targetSalary" placeholder="目标薪资" :min="0" :step="1000" controls-position="right" style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增题目</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        :data="questionList"
        v-loading="loading"
        :border="false"
        :stripe="false"
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
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)"><el-icon><Edit /></el-icon>编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)"><el-icon><Delete /></el-icon>删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && questionList.length === 0" class="empty-state">
        <el-empty description="暂无题目数据" />
      </div>

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

    <div class="back-button-section">
      <el-button @click="goBack" size="large"><el-icon><Back /></el-icon>返回首页</el-button>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="题目名称" prop="questionName">
          <el-input v-model="formData.questionName" placeholder="请输入题目名称（1-200 字）" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="技术方向" prop="direction">
          <el-select v-model="formData.direction" placeholder="请选择技术方向" style="width: 100%">
            <el-option v-for="item in techDirections" :key="item.dictCode" :label="item.dictName" :value="item.dictName" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标薪资" prop="targetSalary">
          <el-input-number v-model="formData.targetSalary" placeholder="请输入目标薪资" :min="0" :step="1000" controls-position="right" style="width: 100%" />
          <div class="form-tip">单位：元</div>
        </el-form-item>
        <el-form-item label="题目描述" prop="questionDesc">
          <el-input v-model="formData.questionDesc" type="textarea" :rows="4" placeholder="请输入题目需求描述" />
        </el-form-item>
        <el-form-item label="选项配置" prop="options">
          <el-input v-model="formData.options" type="textarea" :rows="6" placeholder='请输入 JSON 格式' />
          <div class="form-tip">JSON 数组格式，包含 label 和 value 字段</div>
          <el-button size="small" type="primary" plain @click="formatOptions" :disabled="!formData.options" class="format-json-btn">
            <el-icon><MagicStick /></el-icon>格式化 JSON
          </el-button>
        </el-form-item>
        <el-form-item label="题目解析" prop="analysis">
          <el-input v-model="formData.analysis" type="textarea" :rows="4" placeholder="请输入题目解析（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
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

const searchForm = reactive({ questionName: '', direction: '', targetSalary: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const questionList = ref([])
const loading = ref(false)
const techDirections = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增题目')
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null, questionName: '', questionDesc: '', options: '', targetSalary: null, direction: '', analysis: ''
})

const formRules = {
  questionName: [
    { required: true, message: '请输入题目名称', trigger: 'blur' },
    { min: 1, max: 200, message: '题目名称长度在 1-200 个字符', trigger: 'blur' }
  ],
  direction: [{ required: true, message: '请选择技术方向', trigger: 'change' }],
  targetSalary: [{ required: true, message: '请输入目标薪资', trigger: 'blur' }],
  questionDesc: [{ required: true, message: '请输入题目描述', trigger: 'blur' }],
  options: [
    { required: true, message: '请输入选项配置', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      try { const p = JSON.parse(value); if (!Array.isArray(p)) callback(new Error('选项必须是 JSON 数组格式')); else callback() }
      catch (e) { callback(new Error('选项必须是有效的 JSON 格式')) }
    }, trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

const loadTechDirections = async () => {
  try {
    const res = await getTechDirections()
    if (res.code === 200) techDirections.value = res.data || []
  } catch (error) { console.error('获取技术方向失败:', error) }
}

const loadQuestionList = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize, orderByColumn: 'id', isAsc: 'asc', ...searchForm }
    const res = await getQuestionList(params)
    if (res.code === 200) { questionList.value = res.data.records || []; pagination.total = res.data.total || 0 }
    else ElMessage.error(res.msg || '加载题目列表失败')
  } catch (error) { console.error('加载题目列表失败:', error); ElMessage.error('加载题目列表失败') }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadQuestionList() }
const handleReset = () => { searchForm.questionName = ''; searchForm.direction = ''; searchForm.targetSalary = null; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增题目'; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  dialogTitle.value = '编辑题目'
  Object.assign(formData, { id: row.id, questionName: row.questionName, questionDesc: row.questionDesc, options: row.options, targetSalary: row.targetSalary, direction: row.direction, analysis: row.analysis || '' })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除题目 "${row.questionName}" 吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    const res = await deleteQuestion(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadQuestionList() }
    else ElMessage.error(res.msg || '删除失败')
  } catch (error) { if (error !== 'cancel') { console.error('删除题目失败:', error); ElMessage.error('删除失败') } }
}

const formatOptions = () => {
  if (!formData.options) { ElMessage.warning('请先输入 JSON 内容'); return }
  try { const p = JSON.parse(formData.options); formData.options = JSON.stringify(p, null, 2); ElMessage.success('格式化成功') }
  catch (e) { ElMessage.error('JSON 格式错误，请检查输入') }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = { ...formData }
        const res = formData.id ? await updateQuestion(data) : await addQuestion(data)
        if (res.code === 200) { ElMessage.success(formData.id ? '编辑成功' : '新增成功'); dialogVisible.value = false; loadQuestionList() }
        else ElMessage.error(res.msg || '操作失败')
      } catch (error) { console.error('保存题目失败:', error); ElMessage.error('保存失败') }
      finally { submitLoading.value = false }
    }
  })
}

const resetForm = () => {
  Object.assign(formData, { id: null, questionName: '', questionDesc: '', options: '', targetSalary: null, direction: '', analysis: '' })
  if (formRef.value) formRef.value.clearValidate()
}

const handleSizeChange = (val) => { pagination.pageSize = val; loadQuestionList() }
const handleCurrentChange = (val) => { pagination.pageNum = val; loadQuestionList() }
const goBack = () => { router.push('/home') }

onMounted(() => { loadTechDirections(); loadQuestionList() })
</script>

<style scoped>
.question-manage-container {
  padding: 20px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.search-card, .table-card {
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: var(--radius-lg);
  margin-bottom: 16px;
}

.search-card :deep(.el-card__body) { padding: 16px; }
.table-card :deep(.el-card__body) { padding: 16px; }
.table-card { min-height: 500px; }

.search-form { display: flex; flex-wrap: wrap; gap: 8px; }

.salary-badge { color: var(--neon-cyan); font-weight: 600; }

.empty-state { padding: 40px 0; }

.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--divider); }

.back-button-section { display: flex; justify-content: center; margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--divider); }

.form-tip { font-size: 12px; color: var(--text-muted); margin-top: 5px; }
.format-json-btn { margin-top: 8px; }

@media (max-width: 1200px) { .question-manage-container { padding: 16px 24px; } }
@media (max-width: 768px) {
  .question-manage-container { padding: 12px 16px; }
  .page-title { font-size: 20px; margin-bottom: 16px; }
  .search-form { flex-direction: column; }
  .pagination-container { justify-content: center; }
}
</style>