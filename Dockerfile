FROM eclipse-temurin:17-jdk-alpine as builder
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jdk-alpine
COPY --from=builder /tmp/dependencies/ ./dependencies/
COPY --from=builder /tmp/spring-boot-loader/ ./spring-boot-loader/
COPY --from=builder /tmp/application/ ./application/
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]