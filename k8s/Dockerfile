FROM openjdk:21
WORKDIR /var/app
COPY ./build/libs/project57-1.0.0.jar /var/app/project57-1.0.0.jar

ENV _JAVA_OPTIONS '-Xms254m -Xmx400m'

CMD [ "java", \
"-Dcom.sun.management.jmxremote=true", \
"-Dcom.sun.management.jmxremote.port=8085", \
"-Dcom.sun.management.jmxremote.local.only=false", \
"-Dcom.sun.management.jmxremote.authenticate=false", \
"-Dcom.sun.management.jmxremote.ssl=false", \
"-Dcom.sun.management.jmxremote.rmi.port=8085", \
"-Djava.rmi.server.hostname=localhost", \
"-jar", "/var/app/project57-1.0.0.jar" ]
EXPOSE 8080
EXPOSE 8085
