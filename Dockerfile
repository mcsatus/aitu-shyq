#
# Scala and sbt Dockerfile
#
#

# Pull base image OpenJDK
FROM darecosystem/java8-deb:latest

ENV APP_NAME aitu-shyq-1.0.0.zip
ENV APP_DIR aitu-shyq-1.0.0
ENV RUN_SCRIPT aitu-shyq
ENV LOG_DIR /dar/logs/aitu-shyq
ENV LOG_ARCHIVE_DIR /dar/logs/archive/aitu-shyq

# configs
RUN mkdir -p /root/config/ \
    && mkdir -p $LOG_DIR \
    && mkdir -p $LOG_ARCHIVE_DIR

COPY ./src/main/resources/*logback.xml /root/config/
COPY ./src/main/resources/*.conf /root/config/

WORKDIR /root
COPY ./target/universal/$APP_NAME /root/
RUN unzip -q $APP_NAME
WORKDIR /root/$APP_DIR/bin

# clean zip
RUN rm /root/$APP_NAME

CMD ["/bin/bash", "-c", "./$RUN_SCRIPT -Dconfig.file=/root/config/application.conf -Dlogback.configurationFile=${LOGBACK}"]
