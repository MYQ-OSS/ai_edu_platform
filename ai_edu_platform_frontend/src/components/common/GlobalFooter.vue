<!-- 全局页脚组件 -->
<template>
  <footer class="global-footer">
    <div class="footer-content">
      <p class="copyright">© 2026 AI 教育平台 版权所有</p>
      <div class="beian-info">
        <a href="https://beian.miit.gov.cn/" target="_blank" rel="noreferrer">粤ICP备2026035720号-1</a>
        <span class="separator">|</span>
        <a href="https://beian.mps.gov.cn/#/query/webSearch?code=44010502003967" rel="noreferrer" target="_blank" class="beian-link">
          <img src="/备案图标.png" alt="公安备案图标" class="beian-icon" />
          粤公网安备44010502003967号
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.global-footer {
  background-color: #f5f7fa;
  color: #606266;
  padding: 20px 0;
  margin-top: auto;
  border-top: 1px solid #e4e7ed;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
}

.copyright {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.beian-info {
  font-size: 12px;
  color: #909399;
}

.beian-info a {
  color: #606266;
  text-decoration: none;
  transition: color 0.3s ease;
}

.beian-info a:hover {
  color: #409EFF;
  text-decoration: underline;
}

.beian-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.beian-icon {
  width: 14px;
  height: 14px;
  vertical-align: middle;
}

.separator {
  margin: 0 10px;
  color: #dcdfe6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .global-footer {
    padding: 15px 0;
  }
  
  .copyright {
    font-size: 12px;
    margin-bottom: 8px;
  }
  
  .beian-info {
    font-size: 11px;
  }
  
  .beian-icon {
    width: 12px;
    height: 12px;
  }
  
  .separator {
    margin: 0 6px;
  }
}
</style>
