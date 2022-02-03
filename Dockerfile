FROM openjdk
WORKDIR chat
ADD bin/job4j_chat-1.0.jar app.jar
ENTRYPOINT java -jar app.jar