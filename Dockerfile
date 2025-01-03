FROM openjdk:17
WORKDIR /app
COPY build/libs/hose-1.0.jar app.jar
LABEL authors="JANG"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



