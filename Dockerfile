# 1. 빌드 단계 (Gradle 이미지를 사용해 소스 코드를 빌드)
# Gradle 공식 이미지 사용
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
# 테스트는 건너뛰고 빌드 (속도 향상)
RUN ./gradlew clean build -x test

# 2. 실행 단계 (빌드된 JAR 파일만 가볍게 실행)
# [수정됨] openjdk 대신 eclipse-temurin 사용 (안정적, 표준)
# 실행에는 JDK가 아닌 JRE만 있어도 되므로 -jre 버전을 사용하여 용량을 줄임
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 실행 컨테이너로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 8080 포트 열기
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
