# Stage 1: Build maven project
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src /app/src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dmaven.test.skip

# Stage 2: Build the final image
FROM openjdk:17-jdk-oraclelinux8

ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
