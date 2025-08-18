# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制jar文件到容器中
COPY target/backend-1.0-SNAPSHOT.jar app.jar

# 暴露端口（根据您的应用配置调整）
EXPOSE 8080

# 设置JVM参数（可选，根据需要进行调整）
ENV JAVA_OPTS="-Xmx512m -Xms256m"

#ENV OPENAI_API_KEY="sk-24a3784b82314d2baa5ff05c9bf8f16a"
#ENV MCP_QQ_KEY="EI7BZ-L4Q6T-MOBXC-VKSUY-WACYV-MTB3T"
#ENV MCP_QQ_SECRET_KEY="KH4mBiKHOdbrUXMq2wY11KLOHHH1Y5dw"

# 运行jar包
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 