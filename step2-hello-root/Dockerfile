FROM --platform=linux/amd64 bellsoft/liberica-openjre-debian:17.0.5-8
ARG TARGETPLATFORM
RUN echo "I'm building for $TARGETPLATFORM"
COPY step2-hello-root-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar /app.jar
