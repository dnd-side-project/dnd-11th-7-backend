FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar jjakkak.jar
ENTRYPOINT ["java", "-jar", "jjakkak.jar"]