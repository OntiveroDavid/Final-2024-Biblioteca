FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar clientes-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/clientes-service.jar"]
