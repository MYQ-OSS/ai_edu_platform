# 单阶段构建 Dockerfile (用于 CI/CD 部署)
# ====================================

FROM eclipse-temurin:21-jre-alpine

# 安装 wget（用于健康检查）
RUN apk add --no-cache wget

# 设置维护者信息
LABEL maintainer="ai_edu_platform" \
      version="0.0.1" \
      description="AI Education Platform Backend"

# 设置工作目录
WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 复制上传的 JAR 包 (由 Gitee Go 流水线通过 SCP 传输)
COPY app.jar app.jar

# 修改文件所有者
RUN chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 暴露应用端口
EXPOSE 8080

# JVM 参数优化（针对容器环境）
ENV JAVA_OPTS="-Xms256m -Xmx512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0"

# 健康检查（使用 Actuator 健康端点）
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
