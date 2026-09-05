FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY lib/mysql-connector-j-26.7.0.jar lib/
COPY src/ src/
COPY web/ web/

RUN mkdir -p out && javac -cp "lib/mysql-connector-j-26.7.0.jar" -d out $(find src -name "*.java")

EXPOSE 8080

CMD ["java", "-cp", "out:lib/mysql-connector-j-26.7.0.jar", "com.sunrise.web.HttpServer"]