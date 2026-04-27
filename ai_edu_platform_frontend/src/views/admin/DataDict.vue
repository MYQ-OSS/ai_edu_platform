<!-- 数据字典管理 - 优化版 -->
<template>
  <div class="data-dict-container">
    <h2 class="page-title">字典管理</h2>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典名称">
          <el-input v-model="searchForm.dictName" placeholder="请输入字典名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增字典</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        :data="dictList"
        v-loading="loading"
        :border="false"
        :stripe="false"
        style="width: 100%;"
      >
        <el-table-column prop="dictCode" label="字典编码" width="200" align="center" />
        <el-table-column prop="dictName" label="字典名称" min-width="150" align="center" />
        <el-table-column prop="sort" label="排序号" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
              {{ row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; gap: 8px; justify-content: center;">
              <el-button type="primary" size="small" @click="handleEdit(row)"><el-icon><Edit /></el-icon>编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)"><el-icon><Delete /></el-icon>删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && dictList.length === 0" class="empty-state">
        <el-empty description="暂无字典数据" />
      </div>

      <div class="pagination-container" v-if="dictList.length > 0">
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

    <el-dialog
      v-model="dialogVisible"
      :title="formData.id ? '编辑字典' : '新增字典'"
      width="600px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="formData.dictCode" placeholder="请输入字典编码（唯一标识）" />
          <div class="form-tip">例如：java_backend、master_degree 等</div>
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="formData.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="排序号" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%;" />
          <div class="form-tip">数字越小越靠前</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <div class="back-button-section">
      <el-button @click="goBack" size="large"><el-icon><Back /></el-icon>返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Back } from '@element-plus/icons-vue'
import { getDictList, addDictData, updateDictData, deleteDictData } from '../../api/adminApi'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const searchForm = reactive({ dictName: '' })
const dictList = ref([])

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const formRef = ref(null)
const formData = reactive({ id: null, dictType: 'tech_direction', dictCode: '', dictName: '', sort: 0, status: '0' })

const formRules = {
  dictCode: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { max: 50, message: '字典编码长度不能超过 50 位', trigger: 'blur' }
  ],
  dictName: [
    { required: true, message: '请输入字典名称', trigger: 'blur' },
    { max: 100, message: '字典名称长度不能超过 100 位', trigger: 'blur' }
  ],
  sort: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
}

const loadDictList = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize, orderByColumn: 'id', isAsc: 'asc', ...searchForm }
    const res = await getDictList(params)
    if (res.code === 200) { dictList.value = res.data.records || []; pagination.total = res.data.total || 0 }
    else ElMessage.error(res.msg || '加载失败')
  } catch (error) { console.error('加载字典列表失败:', error); ElMessage.error('加载字典列表失败') }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadDictList() }
const handleReset = () => { searchForm.dictName = ''; handleSearch() }
const handleAdd = () => { dialogVisible.value = true }

const handleEdit = (row) => {
  Object.assign(formData, { id: row.id, dictType: 'tech_direction', dictCode: row.dictCode, dictName: row.dictName, sort: row.sort, status: row.status })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除字典 "${row.dictName}" 吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    const res = await deleteDictData(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadDictList() }
    else ElMessage.error(res.msg || '删除失败')
  } catch (error) { if (error !== 'cancel') { console.error('删除字典失败:', error); ElMessage.error('删除失败') } }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = { ...formData }
        const res = formData.id ? await updateDictData(data) : await addDictData(data)
        if (res.code === 200) { ElMessage.success(formData.id ? '编辑成功' : '新增成功'); dialogVisible.value = false; loadDictList() }
        else ElMessage.error(res.msg || '操作失败')
      } catch (error) { console.error('保存字典失败:', error); ElMessage.error('保存失败') }
      finally { submitLoading.value = false }
    }
  })
}

const resetForm = () => {
  Object.assign(formData, { id: null, dictType: 'tech_direction', dictCode: '', dictName: '', sort: 0, status: '0' })
  if (formRef.value) formRef.value.clearValidate()
}

const handleSizeChange = (val) => { pagination.pageSize = val; loadDictList() }
const handleCurrentChange = (val) => { pagination.pageNum = val; loadDictList() }
const goBack = () => { router.push('/home') }

onMounted(() => { loadDictList() })
</script>

<style scoped>
.data-dict-container {
  padding: 20px 40px;
  max-width: 1400px;
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

.empty-state { padding: 40px 0; }

.pagination-container { margin-top: 16px; display: flex; justify-content: flex-end; }

.back-button-section { display: flex; justify-content: center; margin-top: 24px; padding-top: 16px; border-top: 1px solid var(--divider); }

.form-tip { font-size: 12px; color: var(--text-muted); margin-top: 5px; }

@media (max-width: 1200px) { .data-dict-container { padding: 16px 24px; } }
@media (max-width: 768px) {
  .data-dict-container { padding: 12px 16px; }
  .page-title { font-size: 20px; margin-bottom: 16px; }
  .search-form { flex-direction: column; }
  .pagination-container { justify-content: center; }
}
</style>