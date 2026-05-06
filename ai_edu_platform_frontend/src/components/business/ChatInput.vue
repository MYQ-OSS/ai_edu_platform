<template>
  <div class="chat-input">
    <div class="chat-input__context" v-if="hasContext">
      <div class="context-tags">
        <el-tag
          v-for="quizId in context.quizRecordIds"
          :key="'quiz-' + quizId"
          type="warning"
          closable
          @close="handleRemoveQuiz(quizId)"
        >
          📝 答题记录 #{{ quizId }}
        </el-tag>
        <el-tag
          v-for="salaryId in context.salaryReportIds"
          :key="'salary-' + salaryId"
          type="success"
          closable
          @close="handleRemoveSalary(salaryId)"
        >
          💰 薪资报告 #{{ salaryId }}
        </el-tag>
      </div>
    </div>

    <div class="chat-input__wrapper">
      <div class="chat-input__actions">
        <el-tooltip content="附加答题记录" placement="top">
          <el-button
            :disabled="quizDisabled || !sessionId"
            circle
            @click="handleAddQuiz"
          >
            📝
          </el-button>
        </el-tooltip>
        <el-tooltip content="附加薪资报告" placement="top">
          <el-button
            :disabled="salaryDisabled || !sessionId"
            circle
            @click="handleAddSalary"
          >
            💰
          </el-button>
        </el-tooltip>
      </div>

      <div class="chat-input__textarea-wrapper">
        <textarea
          ref="textareaRef"
          v-model="inputText"
          class="chat-input__textarea"
          :placeholder="placeholder"
          :disabled="!sessionId || sending"
          :maxlength="maxLength"
          @keydown="handleKeyDown"
          @input="handleInputEvent"
          rows="1"
        ></textarea>
        <div class="chat-input__counter" v-if="inputText.length > maxLength * 0.8">
          {{ inputText.length }}/{{ maxLength }}
        </div>
      </div>

      <el-button
        type="primary"
        class="chat-input__send"
        :disabled="!canSend"
        :loading="sending"
        @click="handleSend"
      >
        <el-icon v-if="!sending"><Promotion /></el-icon>
        {{ sending ? "发送中..." : "发送" }}
      </el-button>
    </div>

    <div class="chat-input__hint">
      按 Ctrl + Enter 发送
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from "vue";
import { Promotion } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useChatStore } from "../../store/chatStore";

const chatStore = useChatStore();

const props = defineProps({
  sessionId: {
    type: String,
    default: null,
  },
  sending: {
    type: Boolean,
    default: false,
  },
  maxLength: {
    type: Number,
    default: 2000,
  },
  placeholder: {
    type: String,
    default: "输入消息...（支持附加答题记录或薪资报告）",
  },
});

const emit = defineEmits(["send", "add-quiz", "add-salary", "remove-quiz", "remove-salary"]);

const textareaRef = ref(null);
const inputText = ref("");

// 防抖定时器
let resizeTimer = null;
let inputTimer = null;

// 计算属性
const context = computed(() => chatStore.context);

const hasContext = computed(() => {
  return (
    context.value.quizRecordIds.length > 0 ||
    context.value.salaryReportIds.length > 0
  );
});

const quizDisabled = computed(() => {
  const total =
    context.value.quizRecordIds.length + context.value.salaryReportIds.length;
  return total >= 5;
});

const salaryDisabled = computed(() => {
  const total =
    context.value.quizRecordIds.length + context.value.salaryReportIds.length;
  return total >= 5;
});

const canSend = computed(() => {
  return (
    props.sessionId &&
    inputText.value.trim().length > 0 &&
    inputText.value.length <= props.maxLength &&
    !props.sending
  );
});

// 自动调整textarea高度（带防抖）
const autoResize = () => {
  if (resizeTimer) {
    clearTimeout(resizeTimer);
  }
  resizeTimer = setTimeout(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = "auto";
      textareaRef.value.style.height = Math.min(textareaRef.value.scrollHeight, 150) + "px";
    }
  }, 100); // 100ms防抖，避免频繁计算高度
};

// 输入事件处理（带防抖）
const handleInput = () => {
  if (inputTimer) {
    clearTimeout(inputTimer);
  }
  inputTimer = setTimeout(() => {
    // 触发输入完成后的逻辑（如自动补全、语法检查等）
    // 目前为空，预留扩展点
  }, 300); // 300ms防抖
};

// 键盘事件
const handleKeyDown = (e) => {
  // Ctrl + Enter 发送
  if (e.ctrlKey && e.key === "Enter") {
    e.preventDefault();
    handleSend();
  }
};

// 输入事件（同时触发高度调整和防抖处理）
const handleInputEvent = () => {
  autoResize();
  handleInput();
};

// 发送消息
const handleSend = () => {
  if (!canSend.value) {
    if (!props.sessionId) {
      ElMessage.warning("请先选择一个会话或创建新对话");
    } else if (inputText.value.trim().length === 0) {
      ElMessage.warning("请输入消息内容");
    } else if (inputText.value.length > props.maxLength) {
      ElMessage.warning(`消息长度不能超过${props.maxLength}字符`);
    }
    return;
  }

  emit("send", inputText.value.trim());
};

// 添加答题记录
const handleAddQuiz = () => {
  if (quizDisabled.value) {
    ElMessage.warning("最多只能附加5条内容（答题记录+薪资报告）");
    return;
  }
  emit("add-quiz");
};

// 添加薪资报告
const handleAddSalary = () => {
  if (salaryDisabled.value) {
    ElMessage.warning("最多只能附加5条内容（答题记录+薪资报告）");
    return;
  }
  emit("add-salary");
};

// 移除答题记录
const handleRemoveQuiz = (recordId) => {
  chatStore.removeQuizContext(recordId);
  emit("remove-quiz", recordId);
};

// 移除薪资报告
const handleRemoveSalary = (reportId) => {
  chatStore.removeSalaryContext(reportId);
  emit("remove-salary", reportId);
};

// 清空输入
const clearInput = () => {
  inputText.value = "";
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = "auto";
    }
  });
};

// 聚焦输入框
const focus = () => {
  if (textareaRef.value) {
    textareaRef.value.focus();
  }
};

// 暴露方法
defineExpose({
  clearInput,
  focus,
});

// 监听sending状态变化，成功后清空输入
watch(
  () => props.sending,
  (newVal, oldVal) => {
    if (oldVal && !newVal) {
      // 发送完成，清空输入
      // 不自动清空，等待消息确认
    }
  }
);
</script>

<style scoped>
.chat-input {
  padding: 16px 20px;
  background: rgba(20, 20, 40, 0.9);
  border-top: 1px solid rgba(0, 255, 255, 0.2);
}

.chat-input__context {
  margin-bottom: 12px;
}

.context-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.context-tags .el-tag {
  background: rgba(180, 74, 255, 0.2);
  border-color: rgba(180, 74, 255, 0.4);
  color: #fff;
}

.chat-input__wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: rgba(30, 30, 60, 0.8);
  border: 1px solid rgba(0, 255, 255, 0.2);
  border-radius: 12px;
  padding: 8px 12px;
  transition: border-color 0.3s ease;
}

.chat-input__wrapper:focus-within {
  border-color: rgba(0, 255, 255, 0.5);
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.2);
}

.chat-input__actions {
  display: flex;
  flex-direction: row;
  gap: 8px;
}

.chat-input__actions .el-button {
  width: 40px;
  height: 40px;
  padding: 0;
  font-size: 18px;
  background: rgba(0, 255, 255, 0.1);
  border: 1px solid rgba(0, 255, 255, 0.2);
  color: #00ffff;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-input__actions .el-button:hover:not(:disabled) {
  background: rgba(0, 255, 255, 0.3);
  transform: scale(1.05);
}

.chat-input__actions .el-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.chat-input__textarea-wrapper {
  flex: 1;
  position: relative;
}

.chat-input__textarea {
  width: 100%;
  min-height: 40px;
  max-height: 150px;
  padding: 12px 12px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #fff;
  font-size: 16px;
  line-height: 1.6;
  resize: none;
  outline: none;
  overflow-y: auto;
  display: flex;
  align-items: center;
}

.chat-input__textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.chat-input__textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.chat-input__counter {
  position: absolute;
  bottom: 4px;
  right: 8px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.chat-input__send {
  height: 44px;
  padding: 0 20px;
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%);
  border: none;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  color: #fff !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-input__send:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 255, 255, 0.4);
}

.chat-input__send:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 统一按钮样式 - 渐变背景 */
.chat-input .el-button--default,
.chat-input .el-button {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.chat-input .el-button--default:hover:not(:disabled),
.chat-input .el-button:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}

.chat-input .el-button--primary {
  background: linear-gradient(135deg, #00ffff 0%, #b44aff 100%) !important;
  border: none !important;
  color: #fff !important;
  font-weight: 600 !important;
}

.chat-input .el-button--primary:hover:not(:disabled) {
  box-shadow: 0 0 20px rgba(0, 255, 255, 0.4) !important;
  transform: translateY(-2px) !important;
}

.chat-input__hint {
  margin-top: 8px;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

/* 自定义滚动条 */
.chat-input__textarea::-webkit-scrollbar {
  width: 4px;
}

.chat-input__textarea::-webkit-scrollbar-track {
  background: transparent;
}

.chat-input__textarea::-webkit-scrollbar-thumb {
  background: rgba(0, 255, 255, 0.3);
  border-radius: 2px;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-input {
    padding: 12px 16px;
  }

  .chat-input__actions {
    gap: 6px;
  }

  .chat-input__send {
    padding: 0 16px;
  }
}
</style>
