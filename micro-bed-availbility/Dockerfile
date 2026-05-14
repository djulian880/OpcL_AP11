FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn ./.mvn
COPY mvnw .
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Copier le fichier application-docker.properties
COPY target/classes/application-docker.properties /app/config/application-docker.properties

EXPOSE 4300

ENTRYPOINT ["java", "-jar", "app.jar"]