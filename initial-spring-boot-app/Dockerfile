FROM openjdk:11-jre-slim
COPY initial-spring-boot-app-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
