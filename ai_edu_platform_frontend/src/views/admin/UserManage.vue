<template>
  <div class="user-manage-container">
    <!-- 页面标题 -->
    <h2>用户管理</h2>
    
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="用户状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="用户角色">
          <el-select
            v-model="searchForm.role"
            placeholder="请选择角色"
            clearable
            style="width: 150px"
          >
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表表格 -->
    <el-card class="table-card">
      <el-table
        :data="userList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="username" label="用户名" min-width="120" />
        
        <el-table-column prop="identity" label="身份" min-width="100" />
        
        <el-table-column prop="salary" label="薪资" min-width="100" align="right">
          <template #default="{ row }">
            {{ row.salary ? `¥${row.salary}` : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="answerTimes" label="答题次数" width="100" align="center" />
        
        <el-table-column prop="averageScore" label="平均分" width="100" align="center" />
        
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'warning' : 'info'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleToggleStatus(row)">
              <el-icon><Switch /></el-icon>
              {{ row.status === '0' ? '禁用' : '启用' }}
            </el-button>
            
            <el-button type="warning" size="small" @click="handleResetPassword(row)">
              <el-icon><RefreshRight /></el-icon>
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Back, Switch, RefreshRight } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus, resetUserPassword } from '../../api/adminApi'
import { useUserStore } from '../../store/userStore'

const router = useRouter()
const userStore = useUserStore()

// 搜索表单
const searchForm = reactive({
  username: '',
  status: '',
  role: ''
})

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 用户列表数据
const userList = ref([])
const loading = ref(false)

/**
 * 加载用户列表
 */
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      orderByColumn: 'id',
      isAsc: 'asc',
      ...searchForm
    }
    
    // 过滤空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const res = await getUserList(params)
    
    if (res.code === 200) {
      userList.value = res.data.records
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (error) {
    console.error('查询用户列表失败:', error)
    ElMessage.error('查询用户列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.pageNum = 1
  loadUserList()
}

/**
 * 重置搜索条件
 */
const handleReset = () => {
  searchForm.username = ''
  searchForm.status = ''
  searchForm.role = ''
  pagination.pageNum = 1
  loadUserList()
}

/**
 * 每页条数变化
 */
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUserList()
}

/**
 * 页码变化
 */
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadUserList()
}

/**
 * 切换用户状态（禁用/启用）
 */
const handleToggleStatus = async (row) => {
  const newStatus = row.status === '0' ? '1' : '0'
  const actionText = newStatus === '1' ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${row.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await updateUserStatus(row.id, newStatus)
    
    if (res.code === 200) {
      ElMessage.success(`${actionText}成功`)
      loadUserList()
    } else {
      ElMessage.error(res.msg || `${actionText}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${actionText}用户失败:`, error)
      ElMessage.error(`${actionText}失败`)
    }
  }
}

/**
 * 重置用户密码
 */
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${row.username}" 的密码吗？重置后密码为：123456`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await resetUserPassword(row.id)
    
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
    } else {
      ElMessage.error(res.msg || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }
}

/**
 * 返回首页
 */
const goBack = () => {
  router.push('/home')
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-manage-container {
  padding: 30px 50px;
  max-width: 1400px;
  margin: 0 auto;
  animation: tech-fade-in 0.6s ease-out;
  position: relative;
}

.user-manage-container h2 {
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
.search-card :deep(.el-table),
.table-card :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-border-color: var(--tech-divider);
  background: transparent;
  color: var(--tech-text-primary);
}

.search-card :deep(.el-table th.el-table__cell),
.table-card :deep(.el-table th.el-table__cell) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: var(--tech-text-secondary);
  border-color: var(--tech-divider);
}

.search-card :deep(.el-table td.el-table__cell),
.table-card :deep(.el-table td.el-table__cell) {
  border-color: var(--tech-divider);
  color: var(--tech-text-primary);
}

.search-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell),
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

.search-form :deep(.el-select-dropdown) {
  background: var(--tech-bg-mid);
  border: 1px solid var(--tech-glass-border);
}

.search-form :deep(.el-select-dropdown__item) {
  color: var(--tech-text-primary);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--tech-text-secondary);
  --el-pagination-button-bg-color: var(--tech-glass-bg);
  color: var(--tech-text-secondary);
}

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--tech-divider);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-manage-container {
    padding: 20px 30px;
  }
}

@media (max-width: 768px) {
  .user-manage-container {
    padding: 15px 20px;
  }

  .user-manage-container h2 {
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
  .search-form :deep(.el-select) {
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
  .user-manage-container {
    padding: 10px 15px;
  }

  .user-manage-container h2 {
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
