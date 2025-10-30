# Use a lightweight Java runtime image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy the built jar from project into the container
COPY target/*.jar app.jar

# Expose port your app listens on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
