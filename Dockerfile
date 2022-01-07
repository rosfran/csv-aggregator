
FROM maven:3.8-openjdk-8-slim as builder

WORKDIR /app

##RUN mvn -DskipTests=true clean compile package

COPY pom.xml .

RUN mvn -B -f pom.xml dependency:go-offline

# Copy all other project files and build project
COPY . /app

RUN mvn -B install -DskipTests=true

### BUILD image
FROM openjdk:8-slim

EXPOSE 8081

ENV APP_HOME /app

#Create base app folder

RUN mkdir $APP_HOME

#Create folder with application logs

RUN mkdir $APP_HOME/vol

RUN mkdir $APP_HOME/vol/out

VOLUME $APP_HOME/vol

WORKDIR $APP_HOME

#COPY --chown=gradle:gradle . /home/gradle/src
COPY ./target/*.jar aggregator-0.0.1.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","aggregator-0.0.1.jar" ]
