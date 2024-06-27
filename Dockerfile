# OpenJDK 21 JDK의 슬림 버전을 기반 이미지로 사용
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너의 /app 디렉토리로 복사
COPY build/libs/*.jar /app/spring-h.jar

# 애플리케이션이 사용할 포트 설정
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어 설정
ENTRYPOINT ["java", "-jar", "/app/spring-h.jar"]