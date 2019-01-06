FROM openjdk:8-jre

RUN mkdir /app

WORKDIR /app

ADD ./target/classes /app/classes
ADD ./target/dependency /app/dependency

CMD java -cp ./classes:./dependency/* com.kumuluz.ee.EeApplication