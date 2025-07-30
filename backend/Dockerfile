FROM openjdk:21-jdk-slim
COPY target/*.jar backend.jar
ENTRYPOINT ["java","-jar","/backend.jar"]