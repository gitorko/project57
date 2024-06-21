FROM openjdk:21
WORKDIR /var/app
COPY ./build/libs/project57-1.0.0.jar /var/app/project57-1.0.0.jar

ENV _JAVA_OPTIONS '-Xms250m \
    -Xmx500m \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:MaxGCPauseMillis=200 \
    -XX:ParallelGCThreads=20 \
    -XX:ConcGCThreads=5 \
    -XX:InitiatingHeapOccupancyPercent=70 \
    -Xlog:gc*=info:file=project57-gc.log:time,uptime,level,tags:filecount=5,filesize=100m'

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
