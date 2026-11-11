FROM registry.cn-hangzhou.aliyuncs.com/library/openjdk:17-jdk-slim
WORKDIR /app
COPY target/saas-kb-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
