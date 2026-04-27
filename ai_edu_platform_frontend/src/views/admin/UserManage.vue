<!-- 用户管理 - 优化版 -->
<template>
  <div class="user-manage-container">
    <h2 class="page-title">用户管理</h2>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable style="width: 150px">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        :data="userList"
        v-loading="loading"
        :border="false"
        :stripe="false"
        style="width: 100%"
        :hide-on-default-select="true"
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
            <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">
              {{ row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'warning' : 'info'" size="small">
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

      <div v-if="!loading && userList.length === 0" class="empty-state">
        <el-empty description="暂无用户数据" />
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

const router = useRouter()

const searchForm = reactive({ username: '', status: '', role: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const userList = ref([])
const loading = ref(false)

const loadUserList = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize, orderByColumn: 'id', isAsc: 'asc', ...searchForm }
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) delete params[key]
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

const handleSearch = () => { pagination.pageNum = 1; loadUserList() }
const handleReset = () => { searchForm.username = ''; searchForm.status = ''; searchForm.role = ''; pagination.pageNum = 1; loadUserList() }
const handleSizeChange = (val) => { pagination.pageSize = val; loadUserList() }
const handleCurrentChange = (val) => { pagination.pageNum = val; loadUserList() }

const handleToggleStatus = async (row) => {
  const newStatus = row.status === '0' ? '1' : '0'
  const actionText = newStatus === '1' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}用户 "${row.username}" 吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    const res = await updateUserStatus(row.id, newStatus)
    if (res.code === 200) { ElMessage.success(`${actionText}成功`); loadUserList() }
    else ElMessage.error(res.msg || `${actionText}失败`)
  } catch (error) {
    if (error !== 'cancel') { console.error(`${actionText}用户失败:`, error); ElMessage.error(`${actionText}失败`) }
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？重置后密码为：123456`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    const res = await resetUserPassword(row.id)
    if (res.code === 200) ElMessage.success('密码重置成功')
    else ElMessage.error(res.msg || '密码重置失败')
  } catch (error) {
    if (error !== 'cancel') { console.error('重置密码失败:', error); ElMessage.error('重置密码失败') }
  }
}

const goBack = () => { router.push('/home') }

onMounted(() => { loadUserList() })
</script>

<style scoped>
.user-manage-container {
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

.back-button-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--divider);
}

@media (max-width: 1200px) { .user-manage-container { padding: 16px 24px; } }
@media (max-width: 768px) {
  .user-manage-container { padding: 12px 16px; }
  .page-title { font-size: 20px; margin-bottom: 16px; }
  .search-form { flex-direction: column; }
  .pagination-container { justify-content: center; }
}
</style>