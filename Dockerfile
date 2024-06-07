# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine as builder

# Define the directory containing the JAR files
ARG JAR_DIR=build/libs

# Create a directory in the image to copy JAR files into
RUN mkdir -p /app

# Copy all JAR files from build/libs to /app directory
COPY ${JAR_DIR}/*.jar /app/

# Loop through each JAR file and extract layers
RUN for file in /app/*.jar; do \
        java -Djarmode=layertools -jar "$file" extract; \
    done

# Stage 2: Final stage
FROM eclipse-temurin:17-jdk-alpine

# Create directories to copy dependencies and application files into
RUN mkdir -p /app/dependencies /app/application

# Copy dependencies from the builder stage
COPY --from=builder /app/dependencies/ /app/dependencies/

# Copy Spring Boot loader and application files from the builder stage
COPY --from=builder /app/spring-boot-loader/ /app/spring-boot-loader/
COPY --from=builder /app/application/ /app/application/

# Define entrypoint for the application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
