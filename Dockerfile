FROM bellsoft/liberica-openjdk-debian:21

ARG JAR_FILE=build/libs/*SNAPSHOT.jar

COPY ${JAR_FILE} project.jar

ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar project.jar"]