FROM openjdk:8-alpine

COPY target/uberjar/poet-two.jar /poet-two/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/poet-two/app.jar"]
