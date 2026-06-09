# 멀티 스테이지 빌드: Gradle 포함 이미지로 빌드 (Wrapper JAR 불필요)
FROM gradle:8.7-jdk17-alpine AS build

WORKDIR /app

# 의존성 파일 먼저 복사 (레이어 캐싱 최적화)
COPY build.gradle .
COPY settings.gradle .

# 의존성 미리 다운로드
RUN gradle dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN gradle bootJar --no-daemon

# 실행 환경 (JRE만 포함한 경량 이미지)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 빌드 결과물 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
