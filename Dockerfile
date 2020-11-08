FROM openjdk:14
COPY /target/user-application-0.0.1-SNAPSHOT.jar /wd/myapp.jar
CMD ["java", "-jar", "/wd/myapp.jar"]