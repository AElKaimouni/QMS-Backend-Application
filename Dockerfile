# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and download the project dependencies (this is cached)
COPY pom.xml .

# Download project dependencies (caching step)
RUN mvn dependency:go-offline -B

# Copy the rest of the project files
COPY src ./src

# Package the application (build the JAR file)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-jdk
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Copy the .env file into the container's /app directory
COPY .env .env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]