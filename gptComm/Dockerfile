# 빌드 단계
FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app

# Gradle 캐시를 활용하기 위해 필요한 파일을 먼저 복사
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle

# 의존성을 먼저 다운로드
RUN gradle build --no-daemon --parallel --continue --stacktrace || true

# 소스 파일을 복사하고 빌드
COPY . /app
RUN gradle build --no-daemon --stacktrace

# 실행 단계
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 빌드한 결과물인 JAR 파일을 실행 이미지로 복사
COPY --from=build /app/build/libs/gptCumm.jar /app/app.jar

# 애플리케이션 실행 포트
EXPOSE 8080

# 애플리케이션 실행 명령
CMD ["java", "-jar", "app.jar"]

