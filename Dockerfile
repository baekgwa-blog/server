# 1. Build Stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .

# 실행 권한 부여
RUN chmod +x ./gradlew

# build
RUN ./gradlew clean build -x test

# 2. Run Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
