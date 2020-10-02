FROM openjdk:11.0.8-jre-slim

EXPOSE 8080

VOLUME [ "/config" ]

ENV APPLICATION_CONFIG=/config/application.properties \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport \
                        -XX:MaxRAMPercentage=80.0 \
                        -XX:+UseG1GC \
                        -XX:+HeapDumpOnOutOfMemoryError \
                        -XX:HeapDumpPath=/logs/ \
                        -Dhost.name=localhost \
                        -Dcom.sun.management.jmxremote \
                        -Dcom.sun.management.jmxremote.port=9004 \
                        -Dcom.sun.management.jmxremote.rmi.port=9004 \
                        -Dcom.sun.management.jmxremote.local.only=false \
                        -Dcom.sun.management.jmxremote.authenticate=false \
                        -Dcom.sun.management.jmxremote.ssl=false \
                        -Dfile.encoding=UTF8 \
                        -Djava.rmi.server.hostname=localhost"

COPY [ "target/eh-replication.jar", "/app/eh-replication.jar" ]

ENTRYPOINT [ "java", "-Dlogging.config=/config/logback-spring.xml", "-jar" ]
CMD [ "/app/eh-replication.jar" ]