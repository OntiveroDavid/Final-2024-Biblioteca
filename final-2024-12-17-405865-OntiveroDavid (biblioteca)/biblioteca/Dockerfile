FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar biblioteca-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/biblioteca-service.jar"]
