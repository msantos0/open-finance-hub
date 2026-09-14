FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/open-finance-hub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]