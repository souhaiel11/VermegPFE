FROM openjdk:17-alpine

RUN apk --no-cache add freetype \
    && apk add --no-cache msttcorefonts-installer fontconfig \
    && update-ms-fonts \
    && fc-cache --force

ENV _JAVA_OPTIONS="-Djava.awt.headless=true"

VOLUME /tmp

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
