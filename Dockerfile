FROM eclipse-temurin:17-jre-alpine

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너 내부로 복사 (이름을 app.jar로 변경)
COPY build/libs/*.jar app.jar

# 애플리케이션이 사용하는 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
