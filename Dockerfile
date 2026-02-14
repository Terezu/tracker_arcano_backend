# Estágio 1: Compilação (Build) com Java 21
FROM maven:3-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Run) com Java 21
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
