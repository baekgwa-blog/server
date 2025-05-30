# 1. Build Stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# 2. Run Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경변수는 컨테이너 실행시 -e로 주입, 아래는 단순 참고
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
