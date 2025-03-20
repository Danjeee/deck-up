FROM amazoncorretto:21.0.4-alpine3.18

WORKDIR /app

COPY . .

EXPOSE 8888

RUN chmod +x ./mvnw

RUN ./mvnw clean install

ENTRYPOINT ["./mvnw", "spring-boot:run"]