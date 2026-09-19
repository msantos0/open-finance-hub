# Stage 1 - Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Stage 2 - Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/open-finance-hub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]