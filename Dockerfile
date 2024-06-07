FROM eclipse-temurin:17-jdk-alpine as builder

# ARG JAR_FILE=build/libs/*.jar # Commented out since it's not used directly
ARG JAR_DIR=build/libs          # Define JAR_DIR instead to specify directory
COPY ${JAR_DIR}/*.jar /app/     # Copy all JARs in build/libs to /app directory
RUN java -Djarmode=layertools -jar /app/*.jar extract

FROM eclipse-temurin:17-jdk-alpine

COPY --from=builder dependencies/ ./           # Copy dependencies
COPY --from=builder spring-boot-loader/ ./     # Copy Spring Boot loader
COPY --from=builder application/ ./            # Copy application files

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
