# 多阶段构建 Dockerfile
# ====================================

# 第一阶段：构建阶段
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件（使用阿里云镜像加速）
COPY settings.xml /root/.m2/settings.xml

# 先复制 pom.xml 并下载依赖（利用 Docker 缓存层，依赖不变则不重新下载）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再复制源代码（代码变化不会导致依赖重新下载）
COPY src ./src

# 执行 Maven 打包（跳过测试）
RUN mvn clean package -DskipTests -B

# 第二阶段：运行阶段
FROM eclipse-temurin:21-jre-alpine

# 安装 wget（用于健康检查）
RUN apk add --no-cache wget

# 设置维护者信息
LABEL maintainer="ai_edu_platform"

# 设置工作目录
WORKDIR /app

# 创建非 root 用户运行应用（安全最佳实践）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 从构建阶段复制 JAR 包
COPY --from=builder /build/target/*.jar app.jar

# 修改文件所有者
RUN chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 暴露应用端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
