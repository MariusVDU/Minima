# Build stage
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom.xml first and download dependencies (this layer will be cached)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy source code (only invalidates cache when code changes)
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/java-mysql-docker-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]