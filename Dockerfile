FROM openjdk:8
ADD target/PaymentGateway-0.0.1-SNAPSHOT.jar PaymentGateway-0.0.1-SNAPSHOT.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","PaymentGateway-0.0.1-SNAPSHOT.jar"]