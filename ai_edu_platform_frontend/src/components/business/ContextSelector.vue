<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
  >
    <!-- 加载状态 -->
    <div v-if="loading" class="context-selector__loading">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 空状态 -->
    <div v-else-if="dataList.length === 0" class="context-selector__empty">
      <el-empty :description="emptyText" :image-size="80" />
    </div>

    <!-- 数据列表 -->
    <div v-else class="context-selector__list">
      <div
        v-for="item in dataList"
        :key="item.id"
        class="context-item"
        :class="{ 'context-item--selected': isSelected(item.id) }"
        @click="handleSelect(item)"
      >
        <div class="context-item__content">
          <div class="context-item__header">
            <span class="context-item__title">{{ getItemTitle(item) }}</span>
            <span v-if="item.score !== undefined" class="context-item__score">
              {{ item.score }}分
            </span>
            <span v-if="item.salaryRange" class="context-item__salary">
              {{ item.salaryRange }}
            </span>
          </div>
          <div class="context-item__info">
            <span v-if="item.aiSuggestion">{{ item.aiSuggestion }}</span>
            <span v-else-if="item.experience">{{ item.experience }}</span>
            <span v-else>{{ getItemSummary(item) }}</span>
          </div>
          <div class="context-item__time">
            {{ formatTime(item.createTime) }}
          </div>
        </div>
        <div class="context-item__check">
          <el-icon v-if="isSelected(item.id)"><Check /></el-icon>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize && !loading" class="context-selector__pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        small
        @current-change="handlePageChange"
      />
    </div>

    <template #footer>
      <div class="context-selector__footer">
        <span class="context-selector__selected-count">
          已选择: {{ selectedCount }} / {{ maxSelect }}
        </span>
        <div class="context-selector__actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleConfirm">确定</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { Check } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import * as chatApi from "../../api/chatApi";
import * as questionApi from "../../api/questionApi";
import * as salaryApi from "../../api/salaryApi";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: "quiz", // 'quiz' 或 'salary'
    validator: (value) => ["quiz", "salary"].includes(value),
  },
  userId: {
    type: Number,
    required: true,
  },
  maxSelect: {
    type: Number,
    default: 5,
  },
  existingIds: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(["update:visible", "confirm"]);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit("update:visible", val),
});

const dialogTitle = computed(() => {
  return props.type === "quiz" ? "选择答题记录" : "选择薪资报告";
});

const emptyText = computed(() => {
  return props.type === "quiz" ? "暂无答题记录" : "暂无薪资报告";
});

const loading = ref(false);
const dataList = ref([]);
const selectedIds = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const total = ref(0);

// 已选择的数量
const selectedCount = computed(() => selectedIds.value.length);

// 判断是否已选择
const isSelected = (id) => {
  return selectedIds.value.includes(id) || props.existingIds.includes(id);
};

// 获取选项标题
const getItemTitle = (item) => {
  if (props.type === "quiz") {
    return item.questionName || `答题记录 #${item.id}`;
  } else {
    return `${item.direction || ""} - ${item.city || ""}`;
  }
};

// 获取选项摘要
const getItemSummary = (item) => {
  if (props.type === "quiz") {
    return item.reason || item.analysis || "暂无摘要";
  } else {
    // 对于薪资报告，优先显示AI建议，其次显示工作经历
    return item.aiSuggestion || item.experience || "暂无摘要";
  }
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return "";
  const time = new Date(timeStr);
  return time.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    if (props.type === "quiz") {
      const response = await questionApi.getHistory(props.userId);
      if (response.code === 200) {
        dataList.value = response.data || [];
        total.value = dataList.value.length;
      }
    } else {
      const response = await salaryApi.getHistory(props.userId);
      if (response.code === 200) {
        dataList.value = response.data || [];
        total.value = dataList.value.length;
      }
    }
  } catch (error) {
    console.error("加载数据失败:", error);
    ElMessage.error("加载数据失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// 选择/取消选择
const handleSelect = (item) => {
  const isAlreadySelected = props.existingIds.includes(item.id);
  if (isAlreadySelected) {
    ElMessage.info("该记录已添加");
    return;
  }

  const index = selectedIds.value.indexOf(item.id);
  if (index > -1) {
    selectedIds.value.splice(index, 1);
  } else {
    const currentSelected =
      selectedIds.value.length + props.existingIds.length;
    if (currentSelected >= props.maxSelect) {
      ElMessage.warning(`最多只能选择${props.maxSelect}条`);
      return;
    }
    selectedIds.value.push(item.id);
  }
};

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 取消
const handleCancel = () => {
  selectedIds.value = [];
  dialogVisible.value = false;
};

// 确认
const handleConfirm = () => {
  const allSelected = [...props.existingIds, ...selectedIds.value];
  emit("confirm", {
    type: props.type,
    ids: allSelected,
  });
  selectedIds.value = [];
  dialogVisible.value = false;
};

// 监听弹窗打开
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      selectedIds.value = [];
      currentPage.value = 1;
      loadData();
    }
  }
);
</script>

<style scoped>
.context-selector__loading {
  padding: 20px;
}

.context-selector__empty {
  padding: 40px 20px;
}

.context-selector__list {
  max-height: 400px;
  overflow-y: auto;
}

.context-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: rgba(30, 30, 60, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.context-item:hover {
  background: rgba(0, 255, 255, 0.1);
  border-color: rgba(0, 255, 255, 0.3);
}

.context-item--selected {
  background: rgba(180, 74, 255, 0.2);
  border-color: #b44aff;
}

.context-item__content {
  flex: 1;
  overflow: hidden;
}

.context-item__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.context-item__title {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.context-item__score {
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(0, 255, 255, 0.2);
  color: #00ffff;
  font-size: 12px;
  font-weight: 600;
}

.context-item__salary {
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(180, 74, 255, 0.2);
  color: #b44aff;
  font-size: 12px;
  font-weight: 600;
}

.context-item__info {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.context-item__time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.context-item__check {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #00ffff;
  font-size: 18px;
}

.context-selector__pagination {
  display: flex;
  justify-content: center;
  padding: 16px 0 0;
}

.context-selector__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.context-selector__selected-count {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.context-selector__actions {
  display: flex;
  gap: 8px;
}

/* 自定义滚动条 */
.context-selector__list::-webkit-scrollbar {
  width: 4px;
}

.context-selector__list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.context-selector__list::-webkit-scrollbar-thumb {
  background: rgba(0, 255, 255, 0.3);
  border-radius: 2px;
}

/* 统一按钮样式 - 渐变背景 */
.context-selector .el-button--default,
.context-selector .el-button {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.context-selector .el-button--default:hover:not(:disabled),
.context-selector .el-button:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}

.context-selector .el-button--primary {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.context-selector .el-button--primary:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}
</style>
