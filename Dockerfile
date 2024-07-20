FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar zzaekkac.jar
ENTRYPOINT ["java", "-jar", "zzaekkac.jar"]