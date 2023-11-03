FROM openjdk:19-jdk-alpine
EXPOSE 8080
COPY target/coffee-0.20.jar coffee-server.0.20.jar
ENTRYPOINT ["java","-jar","coffee-server.0.20.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]