# Use OpenJDK 17
FROM openjdk:17-jdk

# Expose port 8080
EXPOSE 8080

# Add the application's jar to the container
ADD target/contactapp.jar contactapp.jar


# The entry point to start the application
ENTRYPOINT ["java", "-jar", "/contactapp.jar"]
