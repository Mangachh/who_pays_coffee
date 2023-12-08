FROM openjdk:19-jdk-alpine
EXPOSE 8080
COPY target/coffee-0.50.jar coffee-server.0.50.jar
ENTRYPOINT ["java","-jar","coffee-server.0.50.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]