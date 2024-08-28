#
# RUN THE APPLICATION
#
FROM amazoncorretto:21-alpine-jdk

WORKDIR /validator

COPY ./target/testbed-shacl-validator.jar testbed-shacl-validator.jar

RUN adduser -D -u 2000 validator
USER validator

CMD ["java", "-jar", "testbed-shacl-validator.jar"]