FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar jjakkak.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "jjakkak.jar"]