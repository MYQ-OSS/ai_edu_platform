<!-- 数据字典管理页面 -->
<template>
  <div class="data-dict-container">
    <h2>字典管理</h2>
    
    <!-- 搜索和操作区域 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典名称">
          <el-input v-model="searchForm.dictName" placeholder="请输入字典名称" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增字典
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 字典列表表格 -->
    <el-card shadow="hover" class="table-card">
      <el-table :data="dictList" border stripe v-loading="loading" style="width: 100%;">
        <el-table-column prop="dictCode" label="字典编码" width="200" align="center" />
        <el-table-column prop="dictName" label="字典名称" min-width="150" align="center" />
        <el-table-column prop="sort" label="排序号" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; gap: 8px; justify-content: center;">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空状态 -->
      <div v-if="!loading && dictList.length === 0" class="empty-state">
        <el-empty description="暂无字典数据" />
      </div>
      
      <!-- 分页 -->
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="formData.id ? '编辑字典' : '新增字典'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="formData.dictCode" placeholder="请输入字典编码（唯一标识）" />
          <div class="form-tip">例如：java_backend、master_degree等</div>
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
    
    <!-- 返回按钮 -->
    <div class="back-button-section">
      <el-button @click="goBack" size="large">
        <el-icon><Back /></el-icon>
        返回首页
      </el-button>
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

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 对话框显示
const dialogVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  dictName: ''
})

// 字典列表
const dictList = ref([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const formRef = ref(null)
const formData = reactive({
  id: null,
  dictType: 'tech_direction',
  dictCode: '',
  dictName: '',
  sort: 0,
  status: '0'
})

// 表单验证规则
const formRules = {
  dictCode: [
    { required: true, message: '请输入字典编码', trigger: 'blur' },
    { max: 50, message: '字典编码长度不能超过50位', trigger: 'blur' }
  ],
  dictName: [
    { required: true, message: '请输入字典名称', trigger: 'blur' },
    { max: 100, message: '字典名称长度不能超过100位', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ]
}

/**
 * 加载字典列表
 */
const loadDictList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      orderByColumn: 'id',
      isAsc: 'asc',
      ...searchForm
    }
    const res = await getDictList(params)
    if (res.code === 200) {
      dictList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载字典列表失败:', error)
    ElMessage.error('加载字典列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.pageNum = 1
  loadDictList()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.dictName = ''
  handleSearch()
}

/**
 * 新增字典
 */
const handleAdd = () => {
  dialogVisible.value = true
}

/**
 * 编辑字典
 */
const handleEdit = (row) => {
  Object.assign(formData, {
    id: row.id,
    dictType: 'tech_direction',
    dictCode: row.dictCode,
    dictName: row.dictName,
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

/**
 * 删除字典
 */
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字典 "${row.dictName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await deleteDictData(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadDictList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除字典失败:', error)
      ElMessage.error('删除失败')
    }
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
          res = await updateDictData(data)
        } else {
          // 新增模式
          res = await addDictData(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(formData.id ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          loadDictList()
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (error) {
        console.error('保存字典失败:', error)
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
    dictType: 'tech_direction',
    dictCode: '',
    dictName: '',
    sort: 0,
    status: '0'
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
  loadDictList()
}

/**
 * 页码变化
 */
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadDictList()
}

/**
 * 返回首页
 */
const goBack = () => {
  router.push('/home')
}

// 页面加载时初始化
onMounted(() => {
  loadDictList()
})
</script>

<style scoped>
.data-dict-container {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
  animation: tech-fade-in 0.6s ease-out;
  position: relative;
}

.data-dict-container h2 {
  margin-bottom: 30px;
  text-align: center;
  font-size: 28px;
  font-weight: 800;
  background: var(--tech-gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search-card,
.table-card {
  background: var(--tech-glass-bg-strong);
  backdrop-filter: blur(var(--tech-glass-blur));
  -webkit-backdrop-filter: blur(var(--tech-glass-blur));
  border: 1px solid var(--tech-glass-border);
  border-radius: var(--tech-radius-lg);
  box-shadow: var(--tech-card-shadow);
}

.search-card :deep(.el-card__body) {
  padding: 20px;
}

.table-card :deep(.el-card__body) {
  padding: 20px;
}

.table-card {
  min-height: 500px;
}

/* 暗色表格 */
.table-card :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-border-color: var(--tech-divider);
  background: transparent;
  color: var(--tech-text-primary);
}

.table-card :deep(.el-table th.el-table__cell) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: var(--tech-text-secondary);
  border-color: var(--tech-divider);
}

.table-card :deep(.el-table td.el-table__cell) {
  border-color: var(--tech-divider);
  color: var(--tech-text-primary);
}

.table-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(255, 255, 255, 0.02);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.search-form :deep(.el-input__wrapper),
.search-form :deep(.el-select .el-input__wrapper) {
  background: var(--tech-glass-bg);
  border: 1px solid var(--tech-glass-border);
  box-shadow: none;
}

.search-form :deep(.el-input__inner) {
  color: var(--tech-text-primary);
}

.search-form :deep(.el-form-item__label) {
  color: var(--tech-text-secondary);
}

.empty-state {
  padding: 40px 0;
}

.empty-state :deep(.el-empty) {
  --el-empty-fill-color-1: var(--tech-accent-cyan);
  color: var(--tech-text-secondary);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--tech-divider);
}

.form-tip {
  font-size: 12px;
  color: var(--tech-text-muted);
  margin-top: 5px;
  line-height: 1.5;
}

/* 对话框毛玻璃 */
.data-dict-container :deep(.el-dialog) {
  background: var(--tech-bg-mid);
  border: 1px solid var(--tech-glass-border);
}

.data-dict-container :deep(.el-dialog__title) {
  color: var(--tech-text-primary);
}

.data-dict-container :deep(.el-dialog__body) {
  color: var(--tech-text-primary);
}

.data-dict-container :deep(.el-dialog__header) {
  border-bottom: 1px solid var(--tech-divider);
}

.data-dict-container :deep(.el-dialog__footer) {
  border-top: 1px solid var(--tech-divider);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .data-dict-container {
    padding: 20px 30px;
  }
}

@media (max-width: 768px) {
  .data-dict-container {
    padding: 15px 20px;
  }

  .data-dict-container h2 {
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
  .data-dict-container {
    padding: 10px 15px;
  }

  .data-dict-container h2 {
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
