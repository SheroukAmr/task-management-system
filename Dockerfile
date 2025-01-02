FROM openjdk:17-jdk-slim

# Update the JAR_FILE name
ARG JAR_FILE=target/Task-Management-System-0.0.1-SNAPSHOT.jar

# Copy the correct JAR file
COPY ${JAR_FILE} app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
