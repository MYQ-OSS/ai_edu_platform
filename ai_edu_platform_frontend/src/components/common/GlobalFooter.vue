<!-- 全局页脚组件 - Cyberpunk 2.0 -->
<template>
  <footer class="global-footer">
    <div class="footer-scanline"></div>
    <div class="footer-content">
      <p class="copyright">© 2026 AI EDU PLATFORM // ALL RIGHTS RESERVED</p>
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
  background: var(--panel-bg-strong);
  color: var(--text-secondary);
  padding: 24px 0 20px;
  margin-top: auto;
  border-top: 1px solid var(--panel-border);
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.footer-scanline {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), var(--neon-purple), transparent);
  animation: footer-scan 4s linear infinite;
  opacity: 0.5;
}

@keyframes footer-scan {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.global-footer::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--neon-cyan) 30%, var(--neon-purple) 60%, transparent 100%);
  opacity: 0.4;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.copyright {
  margin: 0 0 10px 0;
  font-size: 12px;
  color: var(--text-muted);
  letter-spacing: 1px;
}

.beian-info {
  font-size: 11px;
  color: var(--text-muted);
}

.beian-info a {
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--transition-base);
}

.beian-info a:hover {
  color: var(--neon-cyan);
  text-shadow: 0 0 8px rgba(0, 212, 255, 0.3);
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
  color: var(--panel-border);
}

@media (max-width: 768px) {
  .global-footer { padding: 15px 0; }
  .copyright { font-size: 11px; margin-bottom: 8px; letter-spacing: 0.5px; }
  .beian-info { font-size: 10px; }
  .beian-icon { width: 12px; height: 12px; }
  .separator { margin: 0 6px; }
}
</style>
